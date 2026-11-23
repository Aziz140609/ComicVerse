package com.aziz.comicverse.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aziz.comicverse.R
import com.aziz.comicverse.ui.components.SakuraBloomIndicator
import com.aziz.comicverse.ui.components.SakuraFallingBackdrop
import kotlinx.coroutines.delay

/**
 * Japanese Aesthetic Splash Screen for ComicVerse.
 *
 * Features:
 * - Washi paper background with drifting sakura petals
 * - Circular Mount Fuji & Rising Sun emblem with crimson rim
 * - Bold Crimson typography "ComicVerse"
 * - Authentic Japanese Hanko (red seal stamp) and Kanji subtitle
 * - Poetic blooming sakura indicator
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
        targetValue = if (startAnimation) 1f else 0.82f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "SplashScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        // Allow user to admire the Japanese aesthetic before entering Home
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Background: Falling Sakura Petals Backdrop
        SakuraFallingBackdrop(
            modifier = Modifier.fillMaxSize(),
            topOffset = 20.dp
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
            // Official ComicVerse Japanese Logo
            Box(
                modifier = Modifier
                    .size(156.dp)
                    .shadow(16.dp, RoundedCornerShape(34.dp), spotColor = Color(0xFFFF5A8D).copy(alpha = 0.35f))
                    .clip(RoundedCornerShape(34.dp))
            ) {
                Image(
                    painter = painterResource(R.drawable.img_comicverse_logo),
                    contentDescription = "ComicVerse Official Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

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

            Spacer(modifier = Modifier.height(42.dp))

            // Animated Blooming Sakura Life Cycle
            SakuraBloomIndicator(size = 54.dp)
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
