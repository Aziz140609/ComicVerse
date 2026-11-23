package com.aziz.comicverse.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aziz.comicverse.R

/**
 * Data class representing an individual decorative sakura blossom or petal.
 *
 * @param drawableId Resource ID of the petal or flower drawable.
 * @param xRatio Horizontal position ratio (0.0f = left edge, 1.0f = right edge).
 * @param yDp Base vertical offset in dp from top.
 * @param sizeDp Display size in dp.
 * @param alpha Opacity (dense at the top, fading towards the bottom).
 * @param rotationDeg Rotation angle in degrees for natural wind-drift appearance.
 */
private data class SakuraBackdropPetal(
    val drawableId: Int,
    val xRatio: Float,
    val yDp: Dp,
    val sizeDp: Dp,
    val alpha: Float,
    val rotationDeg: Float
)

/**
 * A sophisticated Japanese Sakura Falling Backdrop.
 *
 * Distributes cherry blossoms and petals according to authentic Japanese aesthetic:
 * - Top zone: dense / lebat with blossoms and drifting petal clusters.
 * - Upper-middle: moderately scattered falling petals.
 * - Lower-middle: sparse, diminishing petals with softer opacity.
 * - Bottom: completely vanishes ("sampai hilang"), maintaining clear contrast and zero clutter.
 *
 * @param modifier Layout modifier.
 * @param topOffset Additional vertical offset in dp (useful when header banner is present).
 */
@Composable
fun SakuraFallingBackdrop(
    modifier: Modifier = Modifier,
    topOffset: Dp = 0.dp
) {
    val petals = listOf(
        // ==========================================
        // ZONE 1: TOP DENSE CLUSTER (y: 10dp - 150dp)
        // Authentic blossoms and petals drifting near the top
        // ==========================================
        // Top-Left Cluster
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.04f,
            yDp = 18.dp,
            sizeDp = 42.dp,
            alpha = 0.65f,
            rotationDeg = -14f
        ),
        // Extra Blossom: Mid-Left Breeze
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.28f,
            yDp = 62.dp,
            sizeDp = 34.dp,
            alpha = 0.56f,
            rotationDeg = -32f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_pair,
            xRatio = 0.16f,
            yDp = 42.dp,
            sizeDp = 26.dp,
            alpha = 0.55f,
            rotationDeg = 24f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.08f,
            yDp = 84.dp,
            sizeDp = 18.dp,
            alpha = 0.50f,
            rotationDeg = -28f
        ),
        // Top-Center Breeze
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.44f,
            yDp = 16.dp,
            sizeDp = 20.dp,
            alpha = 0.48f,
            rotationDeg = 38f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.56f,
            yDp = 58.dp,
            sizeDp = 15.dp,
            alpha = 0.42f,
            rotationDeg = -16f
        ),
        // Top-Right Cluster
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petals_clean,
            xRatio = 0.78f,
            yDp = 12.dp,
            sizeDp = 68.dp,
            alpha = 0.60f,
            rotationDeg = 12f
        ),
        // Extra Blossom: Top-Right Near Cluster
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.86f,
            yDp = 30.dp,
            sizeDp = 38.dp,
            alpha = 0.62f,
            rotationDeg = 24f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.72f,
            yDp = 92.dp,
            sizeDp = 19.dp,
            alpha = 0.48f,
            rotationDeg = -32f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.90f,
            yDp = 118.dp,
            sizeDp = 17.dp,
            alpha = 0.52f,
            rotationDeg = 20f
        ),

        // ==========================================
        // ZONE 2: UPPER-MIDDLE (y: 160dp - 320dp)
        // Diminishing density, blossoms & petals drifting downwards
        // ==========================================
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.06f,
            yDp = 175.dp,
            sizeDp = 16.dp,
            alpha = 0.42f,
            rotationDeg = 32f
        ),
        // Extra Blossom: Upper-Middle Right Margin
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.82f,
            yDp = 180.dp,
            sizeDp = 30.dp,
            alpha = 0.44f,
            rotationDeg = 45f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_pair,
            xRatio = 0.85f,
            yDp = 210.dp,
            sizeDp = 22.dp,
            alpha = 0.38f,
            rotationDeg = -18f
        ),
        // Extra Blossom: Upper-Middle Left Margin
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.05f,
            yDp = 235.dp,
            sizeDp = 27.dp,
            alpha = 0.40f,
            rotationDeg = -18f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.28f,
            yDp = 255.dp,
            sizeDp = 14.dp,
            alpha = 0.32f,
            rotationDeg = 42f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.92f,
            yDp = 295.dp,
            sizeDp = 13.dp,
            alpha = 0.30f,
            rotationDeg = -12f
        ),

        // ==========================================
        // ZONE 3: MIDDLE (y: 340dp - 500dp)
        // Sparsely scattered, gentle fading
        // ==========================================
        // Extra Blossom: Fading Blossom in Middle
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.74f,
            yDp = 360.dp,
            sizeDp = 21.dp,
            alpha = 0.26f,
            rotationDeg = 15f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.05f,
            yDp = 365.dp,
            sizeDp = 13.dp,
            alpha = 0.25f,
            rotationDeg = -22f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.82f,
            yDp = 425.dp,
            sizeDp = 12.dp,
            alpha = 0.20f,
            rotationDeg = 26f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.62f,
            yDp = 485.dp,
            sizeDp = 11.dp,
            alpha = 0.16f,
            rotationDeg = -30f
        ),

        // ==========================================
        // ZONE 4: LOWER-MIDDLE (y: 520dp - 620dp)
        // Rare whisper of falling petals before vanishing
        // ==========================================
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.04f,
            yDp = 540.dp,
            sizeDp = 10.dp,
            alpha = 0.12f,
            rotationDeg = 18f
        ),
        SakuraBackdropPetal(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.88f,
            yDp = 595.dp,
            sizeDp = 9.dp,
            alpha = 0.09f,
            rotationDeg = -38f
        )
        // ZONE 5 (620dp+): 0 petals, completely faded out
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        Box(modifier = Modifier.fillMaxSize()) {
            petals.forEach { petal ->
                val xOffset = screenWidth * petal.xRatio
                val effectiveY = petal.yDp + topOffset
                Image(
                    painter = painterResource(petal.drawableId),
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = xOffset, y = effectiveY)
                        .size(petal.sizeDp)
                        .rotate(petal.rotationDeg)
                        .alpha(petal.alpha)
                )
            }
        }
    }
}
