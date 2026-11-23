package com.aziz.comicverse.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aziz.comicverse.R
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

/**
 * Data model for a particle within the SpongeBob-style Sakura Wave Wipe.
 */
private data class SakuraWaveParticle(
    val drawableId: Int,
    val longitudinalOffset: Float, // Position along wave flow (-0.35f to 0.35f)
    val lateralOffset: Float,      // Spread across wave width (-0.65f to 0.65f)
    val sizeDp: Dp,
    val baseAlpha: Float,
    val baseRotation: Float,
    val spinMultiplier: Float,
    val wobbleFrequency: Float
)

/**
 * Japanese Aesthetic Splash Screen for ComicVerse.
 *
 * Features:
 * - Washi paper background.
 * - Stylized Pink ComicVerse Manga Typography with speed swoosh & sakura blossom.
 * - Traditional Japanese Hanko seal stamp (漫画) & Kanji subtitle.
 * - SpongeBob-Style "Sakura Wave Wipe" Transition:
 *   A rich, cascading wave of sakura blossoms and petals sweeping across the screen
 *   from Top-Right to Bottom-Left to reveal the app!
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }
    var startWaveWipe by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "SplashAlpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.86f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "SplashScale"
    )

    // SpongeBob-style Wave Wipe progress: sweeps from 0f (top-right) to 1f (bottom-left)
    val waveProgress by animateFloatAsState(
        targetValue = if (startWaveWipe) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = LinearEasing),
        label = "SakuraWaveWipe"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        // After branding displays, trigger the sweeping Sakura Wave Wipe
        delay(700)
        startWaveWipe = true
        // Right as the thickest part of the wave sweeps over the screen, transition to Home
        delay(1400)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Center Branding Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scaleAnim)
                .alpha(alphaAnim)
                .padding(horizontal = 24.dp)
        ) {
            // Stylized ComicVerse Pink Manga Typography with Swoosh & Sakura Blossom
            Image(
                painter = painterResource(R.drawable.img_comicverse_text_pink),
                contentDescription = "ComicVerse",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.84f)
                    .aspectRatio(875f / 469f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Japanese Subtitle with Hanko Seal Stamp
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "コミックバース • 漫画の世界",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color(0xFF8B1E32)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Traditional Japanese Hanko Seal Stamp
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF8B1E32))
                        .border(1.dp, Color(0xFFF0E7DE), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "漫画",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Slogan
            Text(
                text = "Discover & Read Endless Manga",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Bottom Editorial Footer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(alphaAnim)
                .padding(bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(2.dp)
                    .background(Color(0xFF8B1E32).copy(alpha = 0.35f))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "JAPANESE MANGA READER • v1.0",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        // SpongeBob-Style Sakura Wave Wipe Overlay
        if (startWaveWipe) {
            SakuraWaveWipeOverlay(
                progress = waveProgress,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * SpongeBob-Style Sakura Wave Wipe Overlay.
 *
 * Simulates a sweeping wave/curtain of dozens of blooming sakura blossoms and fluttering petals
 * washing over the entire screen from Top-Right (x=1.1, y=-0.1) to Bottom-Left (x=-0.2, y=1.2).
 */
@Composable
private fun SakuraWaveWipeOverlay(
    progress: Float,
    modifier: Modifier = Modifier
) {
    // Generate deterministic particles for the wave wipe
    val particles = remember {
        val list = mutableListOf<SakuraWaveParticle>()
        val rnd = Random(42) // Fixed seed for stable layout
        val drawables = listOf(
            R.drawable.ic_sakura_flower_clean,
            R.drawable.ic_sakura_petals_clean,
            R.drawable.ic_sakura_petal_curved,
            R.drawable.ic_sakura_petal_falling,
            R.drawable.ic_sakura_petal_pair
        )

        // 180 particles forming a super dense, thick sweeping sakura wave curtain (SpongeBob bubble wipe style)
        for (i in 0 until 180) {
            val dId = when {
                i % 3 == 0 -> R.drawable.ic_sakura_flower_clean   // Full blossoms
                i % 4 == 0 -> R.drawable.ic_sakura_petals_clean   // Petal clusters
                else -> drawables[rnd.nextInt(drawables.size)]
            }
            val size = when (dId) {
                R.drawable.ic_sakura_flower_clean -> (36 + rnd.nextInt(30)).dp // 36dp - 66dp (lush blossoms)
                R.drawable.ic_sakura_petals_clean -> (48 + rnd.nextInt(32)).dp // 48dp - 80dp (thick clusters)
                else -> (24 + rnd.nextInt(20)).dp                              // 24dp - 44dp (fluttering petals)
            }

            list.add(
                SakuraWaveParticle(
                    drawableId = dId,
                    longitudinalOffset = (rnd.nextFloat() - 0.5f) * 1.15f, // Much deeper wave thickness
                    lateralOffset = (rnd.nextFloat() - 0.5f) * 1.65f,      // Broad wave span covering entire screen
                    sizeDp = size,
                    baseAlpha = 0.82f + rnd.nextFloat() * 0.18f,
                    baseRotation = rnd.nextFloat() * 360f,
                    spinMultiplier = (rnd.nextFloat() - 0.5f) * 3.5f,
                    wobbleFrequency = 2f + rnd.nextFloat() * 4f
                )
            )
        }
        list
    }

    BoxWithConstraints(modifier = modifier) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Box(modifier = Modifier.fillMaxSize()) {
            // Wave progress maps from -0.45f (entry) to 1.45f (exit)
            val wavePos = -0.45f + progress * 1.90f

            // Lush translucent sakura pink wash moving diagonally with the wave for SpongeBob wipe body fullness
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerWaveX = size.width * (1.20f - wavePos * 1.50f)
                val centerWaveY = size.height * (-0.20f + wavePos * 1.50f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFB6C1).copy(alpha = 0.60f),
                            Color(0xFFFFC0CB).copy(alpha = 0.40f),
                            Color(0xFFFFD1DC).copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(centerWaveX, centerWaveY),
                        radius = size.width * 1.15f
                    )
                )
            }

            particles.forEach { p ->
                // Position along diagonal stream (Top-Right to Bottom-Left)
                val itemProgress = wavePos + p.longitudinalOffset

                // Diagonal path: X moves from right (1.20) to left (-0.30)
                // Y moves from top (-0.20) to bottom (1.30)
                val baseX = 1.20f - itemProgress * 1.50f
                val baseY = -0.20f + itemProgress * 1.50f

                // Lateral offset (perpendicular to diagonal stream)
                val perpX = p.lateralOffset * 0.65f
                val perpY = p.lateralOffset * 0.65f

                // Sine wave flutter/wobble as wind pushes the wave
                val wobble = sin(progress * Math.PI * p.wobbleFrequency).toFloat() * 16f

                val finalX = screenWidth * (baseX + perpX) + wobble.dp
                val finalY = screenHeight * (baseY + perpY) + (wobble * 0.5f).dp

                val currentRotation = p.baseRotation + progress * 360f * p.spinMultiplier

                Image(
                    painter = painterResource(p.drawableId),
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = finalX, y = finalY)
                        .size(p.sizeDp)
                        .rotate(currentRotation)
                        .alpha(p.baseAlpha)
                )
            }
        }
    }
}
