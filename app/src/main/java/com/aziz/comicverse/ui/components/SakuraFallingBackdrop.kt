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
 * Data class representing a decorative sakura blossom, petal, or branch in the backdrop.
 *
 * @param drawableId Resource ID of the asset.
 * @param xRatio Horizontal position ratio (0.0f = left edge, 1.0f = right edge).
 * @param yDp Base vertical offset in dp from top.
 * @param sizeDp Display size in dp.
 * @param alpha Opacity.
 * @param rotationDeg Rotation angle in degrees for natural wind-drift appearance.
 */
private data class SakuraBackdropElement(
    val drawableId: Int,
    val xRatio: Float,
    val yDp: Dp,
    val sizeDp: Dp,
    val alpha: Float,
    val rotationDeg: Float
)

/**
 * Japanese Sakura Falling Backdrop with Lush Canopy & Continuous Density Gradient.
 *
 * - Paling Atas: Sangat lebat (dense branch canopies, multiple full blossoms & drifting petals).
 * - Bawah Gambar: Sangat lebat (rich clusters of sakura blossoms & petal swarms).
 * - Menengah ke Bawah: Berangsur-angsur menipis namun tetap lumayan banyak sepanjang layar.
 */
@Composable
fun SakuraFallingBackdrop(
    modifier: Modifier = Modifier,
    topOffset: Dp = 0.dp
) {
    val elements = listOf(
        // =========================================================================
        // ZONE 1: TOP CANOPY — SANGAT LEBAT (y: 0dp - 200dp)
        // Ranting sakura melengkung (arch), cabang berbunga lebat, & bunga mekar penuh
        // =========================================================================
        // Arching Branch Canopy Top-Right
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_branch_arch,
            xRatio = 0.65f,
            yDp = (-10).dp,
            sizeDp = 145.dp,
            alpha = 0.78f,
            rotationDeg = 5f
        ),
        // Lush Blooming Branch Top-Left
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_branch_lush,
            xRatio = (-0.08f),
            yDp = 8.dp,
            sizeDp = 115.dp,
            alpha = 0.72f,
            rotationDeg = 25f
        ),
        // Dense Top Blossoms (Bunga Mekar Penuh)
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.03f,
            yDp = 18.dp,
            sizeDp = 44.dp,
            alpha = 0.75f,
            rotationDeg = -14f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.22f,
            yDp = 28.dp,
            sizeDp = 36.dp,
            alpha = 0.68f,
            rotationDeg = 30f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.45f,
            yDp = 14.dp,
            sizeDp = 40.dp,
            alpha = 0.70f,
            rotationDeg = -22f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.86f,
            yDp = 24.dp,
            sizeDp = 42.dp,
            alpha = 0.75f,
            rotationDeg = 18f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.32f,
            yDp = 75.dp,
            sizeDp = 35.dp,
            alpha = 0.65f,
            rotationDeg = -35f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.68f,
            yDp = 85.dp,
            sizeDp = 36.dp,
            alpha = 0.66f,
            rotationDeg = 40f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.12f,
            yDp = 110.dp,
            sizeDp = 38.dp,
            alpha = 0.68f,
            rotationDeg = 12f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.90f,
            yDp = 120.dp,
            sizeDp = 34.dp,
            alpha = 0.68f,
            rotationDeg = -20f
        ),
        // Petal Swarms in Top Zone
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petals_clean,
            xRatio = 0.72f,
            yDp = 45.dp,
            sizeDp = 75.dp,
            alpha = 0.70f,
            rotationDeg = 15f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petals_clean,
            xRatio = 0.14f,
            yDp = 80.dp,
            sizeDp = 65.dp,
            alpha = 0.62f,
            rotationDeg = -25f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_pair,
            xRatio = 0.54f,
            yDp = 50.dp,
            sizeDp = 28.dp,
            alpha = 0.60f,
            rotationDeg = 25f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.07f,
            yDp = 145.dp,
            sizeDp = 20.dp,
            alpha = 0.58f,
            rotationDeg = -30f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.78f,
            yDp = 155.dp,
            sizeDp = 22.dp,
            alpha = 0.55f,
            rotationDeg = 35f
        ),

        // =========================================================================
        // ZONE 2: SUB-HEADER / UPPER-MIDDLE — LEBAT DI BAWAH GAMBAR (y: 200dp - 400dp)
        // Transisi kaya bunga mekar di bawah banner gambar / header
        // =========================================================================
        // Lush Branch Accent on Right Edge
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_branch_lush,
            xRatio = 0.78f,
            yDp = 205.dp,
            sizeDp = 100.dp,
            alpha = 0.65f,
            rotationDeg = -30f
        ),
        // Sub-header Blossoms
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.05f,
            yDp = 210.dp,
            sizeDp = 38.dp,
            alpha = 0.64f,
            rotationDeg = 22f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.28f,
            yDp = 225.dp,
            sizeDp = 32.dp,
            alpha = 0.58f,
            rotationDeg = -18f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.60f,
            yDp = 240.dp,
            sizeDp = 36.dp,
            alpha = 0.60f,
            rotationDeg = 45f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.88f,
            yDp = 255.dp,
            sizeDp = 35.dp,
            alpha = 0.62f,
            rotationDeg = -12f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.15f,
            yDp = 290.dp,
            sizeDp = 30.dp,
            alpha = 0.55f,
            rotationDeg = 33f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.44f,
            yDp = 320.dp,
            sizeDp = 34.dp,
            alpha = 0.54f,
            rotationDeg = -28f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.82f,
            yDp = 345.dp,
            sizeDp = 31.dp,
            alpha = 0.52f,
            rotationDeg = 15f
        ),
        // Petal Swarms in Sub-Header
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petals_clean,
            xRatio = 0.35f,
            yDp = 265.dp,
            sizeDp = 60.dp,
            alpha = 0.52f,
            rotationDeg = 20f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_pair,
            xRatio = 0.08f,
            yDp = 340.dp,
            sizeDp = 24.dp,
            alpha = 0.50f,
            rotationDeg = -22f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.65f,
            yDp = 370.dp,
            sizeDp = 20.dp,
            alpha = 0.48f,
            rotationDeg = 40f
        ),

        // =========================================================================
        // ZONE 3: MIDDLE — BERKURANG TAPI TETAP LUMAYAN BANYAK (y: 400dp - 650dp)
        // =========================================================================
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.06f,
            yDp = 410.dp,
            sizeDp = 28.dp,
            alpha = 0.46f,
            rotationDeg = -15f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.86f,
            yDp = 430.dp,
            sizeDp = 29.dp,
            alpha = 0.45f,
            rotationDeg = 28f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.28f,
            yDp = 480.dp,
            sizeDp = 26.dp,
            alpha = 0.40f,
            rotationDeg = 35f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.72f,
            yDp = 515.dp,
            sizeDp = 27.dp,
            alpha = 0.38f,
            rotationDeg = -24f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.12f,
            yDp = 560.dp,
            sizeDp = 25.dp,
            alpha = 0.36f,
            rotationDeg = 18f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.84f,
            yDp = 610.dp,
            sizeDp = 24.dp,
            alpha = 0.34f,
            rotationDeg = -32f
        ),
        // Drifting Petals in Middle
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petals_clean,
            xRatio = 0.50f,
            yDp = 445.dp,
            sizeDp = 50.dp,
            alpha = 0.38f,
            rotationDeg = -10f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_pair,
            xRatio = 0.40f,
            yDp = 540.dp,
            sizeDp = 22.dp,
            alpha = 0.35f,
            rotationDeg = 15f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.92f,
            yDp = 520.dp,
            sizeDp = 16.dp,
            alpha = 0.35f,
            rotationDeg = -20f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.05f,
            yDp = 625.dp,
            sizeDp = 18.dp,
            alpha = 0.32f,
            rotationDeg = 25f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.58f,
            yDp = 635.dp,
            sizeDp = 16.dp,
            alpha = 0.30f,
            rotationDeg = -14f
        ),

        // =========================================================================
        // ZONE 4: LOWER-MIDDLE — MENIPIS TAPI TETAP BERASA (y: 650dp - 880dp)
        // =========================================================================
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.22f,
            yDp = 680.dp,
            sizeDp = 22.dp,
            alpha = 0.28f,
            rotationDeg = -26f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.78f,
            yDp = 730.dp,
            sizeDp = 21.dp,
            alpha = 0.26f,
            rotationDeg = 20f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.08f,
            yDp = 810.dp,
            sizeDp = 20.dp,
            alpha = 0.22f,
            rotationDeg = 15f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_pair,
            xRatio = 0.88f,
            yDp = 780.dp,
            sizeDp = 20.dp,
            alpha = 0.25f,
            rotationDeg = -18f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.48f,
            yDp = 820.dp,
            sizeDp = 16.dp,
            alpha = 0.22f,
            rotationDeg = 30f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.70f,
            yDp = 865.dp,
            sizeDp = 15.dp,
            alpha = 0.20f,
            rotationDeg = -22f
        ),

        // =========================================================================
        // ZONE 5: LOWER / BOTTOM — HALUS & BERTAHAP HILANG (y: 880dp - 1050dp)
        // =========================================================================
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.12f,
            yDp = 910.dp,
            sizeDp = 14.dp,
            alpha = 0.16f,
            rotationDeg = 18f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.85f,
            yDp = 955.dp,
            sizeDp = 13.dp,
            alpha = 0.14f,
            rotationDeg = -35f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.42f,
            yDp = 1010.dp,
            sizeDp = 12.dp,
            alpha = 0.11f,
            rotationDeg = 24f
        )
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        Box(modifier = Modifier.fillMaxSize()) {
            elements.forEach { item ->
                val xOffset = screenWidth * item.xRatio
                val effectiveY = item.yDp + topOffset
                Image(
                    painter = painterResource(item.drawableId),
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = xOffset, y = effectiveY)
                        .size(item.sizeDp)
                        .rotate(item.rotationDeg)
                        .alpha(item.alpha)
                )
            }
        }
    }
}
