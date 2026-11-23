package com.aziz.comicverse.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated Sakura Lifecycle Indicator (Mono no Aware aesthetic):
 * Biji (Seed / Bud) -> Mekar (Bloom) -> Layu / Gugur (Withering & Falling Petals) -> Kembali ke Biji.
 *
 * Loops infinitely during loading states.
 */
@Composable
fun SakuraBloomIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SakuraLifecycle")
    val cycleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CycleProgress"
    )

    Canvas(modifier = modifier.size(size)) {
        val centerX = this.size.width / 2f
        val centerY = this.size.height / 2f
        val maxRadius = this.size.minDimension * 0.42f

        drawSakuraLifecycle(
            progress = cycleProgress,
            center = Offset(centerX, centerY),
            maxRadius = maxRadius
        )
    }
}

/**
 * Draws the complete life cycle of Sakura based on normalized progress [0f..1f].
 */
private fun DrawScope.drawSakuraLifecycle(
    progress: Float,
    center: Offset,
    maxRadius: Float
) {
    // 4 Phases:
    // 0.00 - 0.22: Phase 1 - Biji & Kuncup (Seed / Sprouting Bud)
    // 0.22 - 0.55: Phase 2 - Mekar Sempurna (Blooming into Full Flower)
    // 0.55 - 0.82: Phase 3 - Layu & Kelopak Berguguran (Withering & Falling Petals)
    // 0.82 - 1.00: Phase 4 - Kembali Menjadi Biji (Contracting back into Seed)

    when {
        // ==========================================================
        // PHASE 1: Biji & Kuncup (0.00 .. 0.22)
        // ==========================================================
        progress < 0.22f -> {
            val t = progress / 0.22f
            val budScale = 0.25f + 0.35f * FastOutSlowInEasing.transform(t)

            // Central Seed/Bud
            drawSeedBud(
                center = center,
                radius = maxRadius * budScale,
                growthProgress = t
            )
        }

        // ==========================================================
        // PHASE 2: Mekar Sempurna (0.22 .. 0.55)
        // ==========================================================
        progress < 0.55f -> {
            val t = (progress - 0.22f) / 0.33f
            val bloomFactor = FastOutSlowInEasing.transform(t)

            val petalLength = maxRadius * (0.4f + 0.6f * bloomFactor)
            val petalWidth = petalLength * 0.52f
            val flowerAlpha = 1f

            // Draw 5 unfurling petals
            for (i in 0 until 5) {
                val baseAngle = i * 72f
                rotate(degrees = baseAngle, pivot = center) {
                    drawNotchedPetal(
                        origin = center,
                        length = petalLength,
                        width = petalWidth,
                        alpha = flowerAlpha,
                        openingRatio = bloomFactor
                    )
                }
            }

            // Stamen & Golden pollen dots fade in
            if (t > 0.3f) {
                val stamenAlpha = ((t - 0.3f) / 0.7f).coerceIn(0f, 1f)
                drawStamen(center = center, radius = maxRadius * 0.28f * bloomFactor, alpha = stamenAlpha)
            }

            // Center blossom pistil
            drawCircle(
                color = Color(0xFF8B1E32),
                radius = maxRadius * 0.12f,
                center = center
            )
        }

        // ==========================================================
        // PHASE 3: Layu & Kelopak Berguguran (0.55 .. 0.82)
        // ==========================================================
        progress < 0.82f -> {
            val t = (progress - 0.55f) / 0.27f
            val witherEase = FastOutSlowInEasing.transform(t)

            val petalLength = maxRadius
            val petalWidth = petalLength * 0.52f
            val fadeAlpha = (1f - witherEase).coerceIn(0f, 1f)

            // Drift distances: petals detach, float outwards and fall downwards in the wind
            for (i in 0 until 5) {
                val baseAngle = i * 72f
                val rad = Math.toRadians(baseAngle.toDouble())
                // Radial outward drift
                val radialDrift = witherEase * maxRadius * 0.55f
                // Wind gravity drift downwards and slight horizontal drift
                val windX = sin(rad).toFloat() * radialDrift + (witherEase * 12f)
                val windY = cos(rad).toFloat() * radialDrift + (witherEase * 28f)
                val flutterRotate = witherEase * (if (i % 2 == 0) 35f else -35f)

                translate(left = windX, top = windY) {
                    rotate(degrees = baseAngle + flutterRotate, pivot = center) {
                        drawNotchedPetal(
                            origin = center,
                            length = petalLength * (1f - 0.15f * witherEase),
                            width = petalWidth * (1f - 0.2f * witherEase),
                            alpha = fadeAlpha,
                            openingRatio = 1f
                        )
                    }
                }
            }

            // Stamen fades out quickly
            if (fadeAlpha > 0.4f) {
                drawStamen(center = center, radius = maxRadius * 0.28f, alpha = (fadeAlpha - 0.4f) / 0.6f)
            }

            // Center core turning into seed
            val seedColor = Color(
                red = 0.55f + 0.15f * witherEase,
                green = 0.25f + 0.15f * witherEase,
                blue = 0.25f + 0.10f * witherEase,
                alpha = 1f
            )
            drawCircle(
                color = seedColor,
                radius = maxRadius * (0.12f + 0.08f * (1f - witherEase)),
                center = center
            )
        }

        // ==========================================================
        // PHASE 4: Kembali Menjadi Biji (0.82 .. 1.00)
        // ==========================================================
        else -> {
            val t = (progress - 0.82f) / 0.18f
            val contractEase = FastOutSlowInEasing.transform(t)

            // Contracting to a calm seed ready for next rebirth
            val currentRadius = maxRadius * (0.20f - 0.08f * contractEase)
            drawSeedBud(
                center = center,
                radius = currentRadius,
                growthProgress = 0f
            )
        }
    }
}

/**
 * Draws the seed and emerging fresh green/pink bud.
 */
private fun DrawScope.drawSeedBud(
    center: Offset,
    radius: Float,
    growthProgress: Float
) {
    // Seed Body (Earthy warm brown-rose gradient)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFE8A598),
                Color(0xFF9E5A63),
                Color(0xFF5D3A3A)
            ),
            center = center,
            radius = radius * 1.2f
        ),
        radius = radius,
        center = center
    )

    // Emerging green sepal bud tips
    if (growthProgress > 0.2f) {
        val sepalAlpha = ((growthProgress - 0.2f) / 0.8f).coerceIn(0f, 1f)
        for (j in 0 until 3) {
            val angle = j * 120f + (growthProgress * 25f)
            val rad = Math.toRadians(angle.toDouble())
            val sepalOffset = Offset(
                x = center.x + cos(rad).toFloat() * radius * 0.8f,
                y = center.y + sin(rad).toFloat() * radius * 0.8f
            )
            drawCircle(
                color = Color(0xFF6B8E23).copy(alpha = sepalAlpha * 0.8f),
                radius = radius * 0.35f,
                center = sepalOffset
            )
        }
    }
}

/**
 * Draws an authentic Japanese Sakura notched petal with gradient shading.
 */
private fun DrawScope.drawNotchedPetal(
    origin: Offset,
    length: Float,
    width: Float,
    alpha: Float,
    openingRatio: Float
) {
    val actualWidth = width * openingRatio
    val actualLength = length

    val path = Path().apply {
        moveTo(origin.x, origin.y)
        // Left curve up towards petal tip
        cubicTo(
            origin.x - actualWidth * 0.55f, origin.y - actualLength * 0.40f,
            origin.x - actualWidth * 0.65f, origin.y - actualLength * 0.88f,
            origin.x - actualWidth * 0.22f, origin.y - actualLength
        )
        // Sakura tip notch / cleft
        quadraticTo(
            origin.x, origin.y - actualLength * 0.84f,
            origin.x + actualWidth * 0.22f, origin.y - actualLength
        )
        // Right curve down towards origin base
        cubicTo(
            origin.x + actualWidth * 0.65f, origin.y - actualLength * 0.88f,
            origin.x + actualWidth * 0.55f, origin.y - actualLength * 0.40f,
            origin.x, origin.y
        )
        close()
    }

    // Outer Petal Blush Gradient (Pale Sakura White-Pink to Rosy Mauve)
    val petalBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF8B1E32).copy(alpha = alpha * 0.95f),  // Crimson Base
            Color(0xFFE898A8).copy(alpha = alpha * 0.90f),  // Rose Mid
            Color(0xFFFBE4E8).copy(alpha = alpha * 0.85f)   // Soft White-Pink Tip
        ),
        start = origin,
        end = Offset(origin.x, origin.y - actualLength)
    )

    drawPath(path = path, brush = petalBrush, style = Fill)
}

/**
 * Draws stamen filaments with golden yellow pollen tips.
 */
private fun DrawScope.drawStamen(
    center: Offset,
    radius: Float,
    alpha: Float
) {
    val count = 8
    val stamenColor = Color(0xFFD4A017).copy(alpha = alpha)
    val filamentColor = Color(0xFF8B1E32).copy(alpha = alpha * 0.6f)

    for (k in 0 until count) {
        val angle = k * (360f / count) + 15f
        val rad = Math.toRadians(angle.toDouble())
        val tipX = center.x + cos(rad).toFloat() * radius
        val tipY = center.y + sin(rad).toFloat() * radius

        // Filament
        drawLine(
            color = filamentColor,
            start = center,
            end = Offset(tipX, tipY),
            strokeWidth = 1.5f
        )
        // Pollen dot
        drawCircle(
            color = stamenColor,
            radius = 2.5f,
            center = Offset(tipX, tipY)
        )
    }
}
