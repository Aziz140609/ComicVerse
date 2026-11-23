package com.aziz.comicverse.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aziz.comicverse.R
import kotlinx.coroutines.delay

/**
 * Data class representing a petal or blossom along the diagonal wave stream.
 */
private data class SakuraWaveElement(
    val drawableId: Int,
    val xRatio: Float,
    val yRatio: Float,
    val sizeDp: Dp,
    val alpha: Float,
    val rotationDeg: Float
)

/**
 * Japanese Aesthetic Splash Screen for ComicVerse.
 *
 * Design:
 * - Washi paper background.
 * - Diagonal sakura flower wave streaming from Top-Right to Bottom-Left corner.
 * - Stylized Pink ComicVerse Manga Typography with speed swoosh and sakura blossom.
 * - Traditional Japanese Hanko seal stamp (漫画) & Kanji subtitle.
 * - Clean, distraction-free (no loading animation in splash).
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "SplashAlpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.85f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "SplashScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Diagonal Sakura Stream from Top-Right to Bottom-Left
        SakuraDiagonalWaveStream(
            modifier = Modifier.fillMaxSize()
        )

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
    }
}

/**
 * Renders a cinematic diagonal stream / wave of sakura blossoms and drifting petals
 * flowing from the Top-Right corner to the Bottom-Left corner.
 */
@Composable
private fun SakuraDiagonalWaveStream(
    modifier: Modifier = Modifier
) {
    // Subtle breeze drift animation
    val infiniteTransition = rememberInfiniteTransition(label = "SakuraWaveBreeze")
    val breezeOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreezeOffset"
    )

    val waveElements = remember {
        listOf(
            // 1. Top-Right Corner (Titik Masuk Gelombang)
            SakuraWaveElement(R.drawable.ic_sakura_flower_clean, 0.94f, 0.03f, 40.dp, 0.85f, 130f),
            SakuraWaveElement(R.drawable.ic_sakura_petals_clean, 0.86f, 0.07f, 50.dp, 0.78f, 140f),
            SakuraWaveElement(R.drawable.ic_sakura_petal_curved, 0.92f, 0.11f, 22.dp, 0.72f, 125f),
            SakuraWaveElement(R.drawable.ic_sakura_flower_clean, 0.80f, 0.14f, 36.dp, 0.80f, 135f),

            // 2. Aliran Menengah Atas
            SakuraWaveElement(R.drawable.ic_sakura_petal_pair, 0.74f, 0.20f, 26.dp, 0.72f, 145f),
            SakuraWaveElement(R.drawable.ic_sakura_flower_clean, 0.68f, 0.26f, 34.dp, 0.75f, 120f),
            SakuraWaveElement(R.drawable.ic_sakura_petal_falling, 0.62f, 0.32f, 20.dp, 0.68f, 140f),

            // 3. Aliran Tengah (Menyilang di sekitar area judul)
            SakuraWaveElement(R.drawable.ic_sakura_flower_clean, 0.58f, 0.38f, 32.dp, 0.70f, 130f),
            SakuraWaveElement(R.drawable.ic_sakura_petals_clean, 0.50f, 0.44f, 52.dp, 0.68f, 135f),
            SakuraWaveElement(R.drawable.ic_sakura_flower_clean, 0.44f, 0.50f, 34.dp, 0.72f, 140f),
            SakuraWaveElement(R.drawable.ic_sakura_petal_curved, 0.40f, 0.56f, 22.dp, 0.68f, 125f),

            // 4. Aliran Menengah Bawah
            SakuraWaveElement(R.drawable.ic_sakura_flower_clean, 0.34f, 0.62f, 34.dp, 0.74f, 135f),
            SakuraWaveElement(R.drawable.ic_sakura_petal_pair, 0.28f, 0.68f, 26.dp, 0.70f, 145f),
            SakuraWaveElement(R.drawable.ic_sakura_flower_clean, 0.24f, 0.74f, 36.dp, 0.76f, 130f),
            SakuraWaveElement(R.drawable.ic_sakura_petal_curved, 0.18f, 0.80f, 22.dp, 0.72f, 140f),

            // 5. Pojok Kiri Bawah (Titik Keluar Gelombang)
            SakuraWaveElement(R.drawable.ic_sakura_flower_clean, 0.14f, 0.85f, 38.dp, 0.80f, 125f),
            SakuraWaveElement(R.drawable.ic_sakura_petals_clean, 0.08f, 0.90f, 50.dp, 0.76f, 140f),
            SakuraWaveElement(R.drawable.ic_sakura_flower_clean, 0.03f, 0.94f, 40.dp, 0.82f, 135f),
            SakuraWaveElement(R.drawable.ic_sakura_petal_falling, 0.02f, 0.88f, 20.dp, 0.70f, 130f)
        )
    }

    BoxWithConstraints(modifier = modifier) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Box(modifier = Modifier.fillMaxSize()) {
            waveElements.forEachIndexed { index, item ->
                val xPos = screenWidth * item.xRatio + (breezeOffset * (if (index % 2 == 0) 1f else -1f)).dp
                val yPos = screenHeight * item.yRatio + (breezeOffset * 0.5f).dp

                Image(
                    painter = painterResource(item.drawableId),
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = xPos, y = yPos)
                        .size(item.sizeDp)
                        .rotate(item.rotationDeg + breezeOffset * 0.8f)
                        .alpha(item.alpha)
                )
            }
        }
    }
}
