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
 * Japanese Sakura Falling Backdrop with Evenly Distributed Canopy & Wall-Anchored Branches.
 *
 * - Batang Kiri: Menempel erat di dinding layar kiri atas (flush against left wall).
 * - Batang Kanan: Menempel di dinding layar kanan dan DITURUNKAN KE BAWAH (flush against right wall, y: ~270dp).
 * - Bagian Atas: Sangat lebat namun DIRATAKAN merata horizontal tanpa tumpukan mengelompok.
 * - Bagian Bawah: Menipis secara bertahap namun tetap tersebar merata.
 */
@Composable
fun SakuraFallingBackdrop(
    modifier: Modifier = Modifier,
    topOffset: Dp = 0.dp
) {
    val elements = listOf(
        // =========================================================================
        // 1. RANTING MENEMPEL DI DINDING LAYAR (WALL-ANCHORED BRANCHES)
        // =========================================================================
        // Batang Kiri: Menempel rapat di dinding kiri atas
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_branch_lush,
            xRatio = -0.06f,
            yDp = 8.dp,
            sizeDp = 120.dp,
            alpha = 0.76f,
            rotationDeg = 18f
        ),
        // Batang Kanan: Menempel di dinding kanan dan DITURUNKAN KE BAWAH (~270dp)
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_branch_arch,
            xRatio = 0.74f,
            yDp = 270.dp,
            sizeDp = 135.dp,
            alpha = 0.75f,
            rotationDeg = -10f
        ),

        // =========================================================================
        // ZONE 1: TOP CANOPY — SANGAT LEBAT & DIRATAKAN MERATA (y: 10dp - 190dp)
        // Tersebar merata dari kiri ke kanan tanpa tumpukan di satu titik
        // =========================================================================
        // Gelombang 1 (y: 15dp - 35dp) — Rata horizontal di puncak layar
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.10f,
            yDp = 22.dp,
            sizeDp = 36.dp,
            alpha = 0.74f,
            rotationDeg = -14f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.28f,
            yDp = 15.dp,
            sizeDp = 38.dp,
            alpha = 0.70f,
            rotationDeg = 25f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.46f,
            yDp = 28.dp,
            sizeDp = 40.dp,
            alpha = 0.72f,
            rotationDeg = -18f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.64f,
            yDp = 16.dp,
            sizeDp = 36.dp,
            alpha = 0.70f,
            rotationDeg = 32f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.84f,
            yDp = 26.dp,
            sizeDp = 38.dp,
            alpha = 0.72f,
            rotationDeg = -20f
        ),

        // Gelombang 2 (y: 65dp - 110dp) — Mengisi celah di antara gelombang 1
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.04f,
            yDp = 80.dp,
            sizeDp = 32.dp,
            alpha = 0.68f,
            rotationDeg = 15f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petals_clean,
            xRatio = 0.20f,
            yDp = 90.dp,
            sizeDp = 50.dp,
            alpha = 0.62f,
            rotationDeg = -12f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.38f,
            yDp = 72.dp,
            sizeDp = 34.dp,
            alpha = 0.68f,
            rotationDeg = -30f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_pair,
            xRatio = 0.55f,
            yDp = 88.dp,
            sizeDp = 26.dp,
            alpha = 0.60f,
            rotationDeg = 22f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.74f,
            yDp = 78.dp,
            sizeDp = 34.dp,
            alpha = 0.68f,
            rotationDeg = 40f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.90f,
            yDp = 98.dp,
            sizeDp = 32.dp,
            alpha = 0.66f,
            rotationDeg = -15f
        ),

        // Gelombang 3 (y: 125dp - 185dp) — Melayang turun secara teratur
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.12f,
            yDp = 140.dp,
            sizeDp = 30.dp,
            alpha = 0.62f,
            rotationDeg = 28f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.30f,
            yDp = 155.dp,
            sizeDp = 20.dp,
            alpha = 0.58f,
            rotationDeg = -25f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.48f,
            yDp = 135.dp,
            sizeDp = 32.dp,
            alpha = 0.64f,
            rotationDeg = -16f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.66f,
            yDp = 160.dp,
            sizeDp = 19.dp,
            alpha = 0.56f,
            rotationDeg = 35f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.82f,
            yDp = 145.dp,
            sizeDp = 30.dp,
            alpha = 0.62f,
            rotationDeg = 18f
        ),

        // =========================================================================
        // ZONE 2: SUB-HEADER / TRANSISI BAWAH GAMBAR (y: 200dp - 390dp)
        // =========================================================================
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.05f,
            yDp = 215.dp,
            sizeDp = 30.dp,
            alpha = 0.60f,
            rotationDeg = 20f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.25f,
            yDp = 230.dp,
            sizeDp = 28.dp,
            alpha = 0.55f,
            rotationDeg = -22f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.48f,
            yDp = 220.dp,
            sizeDp = 30.dp,
            alpha = 0.58f,
            rotationDeg = 35f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petals_clean,
            xRatio = 0.68f,
            yDp = 232.dp,
            sizeDp = 46.dp,
            alpha = 0.52f,
            rotationDeg = 12f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.14f,
            yDp = 285.dp,
            sizeDp = 28.dp,
            alpha = 0.54f,
            rotationDeg = 15f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.38f,
            yDp = 310.dp,
            sizeDp = 28.dp,
            alpha = 0.52f,
            rotationDeg = -28f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.60f,
            yDp = 330.dp,
            sizeDp = 27.dp,
            alpha = 0.50f,
            rotationDeg = 42f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.88f,
            yDp = 350.dp,
            sizeDp = 18.dp,
            alpha = 0.46f,
            rotationDeg = -14f
        ),

        // =========================================================================
        // ZONE 3: MIDDLE — BERKURANG TAPI TETAP LUMAYAN BANYAK (y: 400dp - 650dp)
        // =========================================================================
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.08f,
            yDp = 415.dp,
            sizeDp = 25.dp,
            alpha = 0.44f,
            rotationDeg = -18f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.35f,
            yDp = 440.dp,
            sizeDp = 25.dp,
            alpha = 0.42f,
            rotationDeg = 25f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.62f,
            yDp = 425.dp,
            sizeDp = 26.dp,
            alpha = 0.42f,
            rotationDeg = -30f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.88f,
            yDp = 460.dp,
            sizeDp = 24.dp,
            alpha = 0.40f,
            rotationDeg = 15f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.48f,
            yDp = 475.dp,
            sizeDp = 18.dp,
            alpha = 0.36f,
            rotationDeg = 20f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.20f,
            yDp = 510.dp,
            sizeDp = 24.dp,
            alpha = 0.38f,
            rotationDeg = 32f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.50f,
            yDp = 535.dp,
            sizeDp = 24.dp,
            alpha = 0.36f,
            rotationDeg = -20f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.78f,
            yDp = 560.dp,
            sizeDp = 22.dp,
            alpha = 0.34f,
            rotationDeg = 28f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.06f,
            yDp = 580.dp,
            sizeDp = 17.dp,
            alpha = 0.32f,
            rotationDeg = -22f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_pair,
            xRatio = 0.86f,
            yDp = 620.dp,
            sizeDp = 20.dp,
            alpha = 0.30f,
            rotationDeg = 16f
        ),

        // =========================================================================
        // ZONE 4: LOWER-MIDDLE — MENIPIS BERTAHAP (y: 650dp - 880dp)
        // =========================================================================
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.12f,
            yDp = 680.dp,
            sizeDp = 21.dp,
            alpha = 0.28f,
            rotationDeg = -24f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.42f,
            yDp = 720.dp,
            sizeDp = 20.dp,
            alpha = 0.26f,
            rotationDeg = 18f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_flower_clean,
            xRatio = 0.76f,
            yDp = 750.dp,
            sizeDp = 20.dp,
            alpha = 0.24f,
            rotationDeg = -15f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_pair,
            xRatio = 0.24f,
            yDp = 800.dp,
            sizeDp = 18.dp,
            alpha = 0.22f,
            rotationDeg = 25f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.85f,
            yDp = 840.dp,
            sizeDp = 15.dp,
            alpha = 0.20f,
            rotationDeg = -30f
        ),

        // =========================================================================
        // ZONE 5: LOWER / BOTTOM — HALUS & BERTAHAP HILANG (y: 880dp - 1050dp)
        // =========================================================================
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.18f,
            yDp = 910.dp,
            sizeDp = 14.dp,
            alpha = 0.16f,
            rotationDeg = 20f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_falling,
            xRatio = 0.54f,
            yDp = 960.dp,
            sizeDp = 13.dp,
            alpha = 0.14f,
            rotationDeg = -35f
        ),
        SakuraBackdropElement(
            drawableId = R.drawable.ic_sakura_petal_curved,
            xRatio = 0.82f,
            yDp = 1010.dp,
            sizeDp = 12.dp,
            alpha = 0.11f,
            rotationDeg = 15f
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
