package com.aziz.comicverse.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.aziz.comicverse.core.designsystem.ComicColorTokens
import com.aziz.comicverse.core.designsystem.ComicShapes

private val LightColorScheme = lightColorScheme(
    primary = ComicColorTokens.LightPrimary,
    onPrimary = ComicColorTokens.LightOnPrimary,
    primaryContainer = ComicColorTokens.LightSakura,
    onPrimaryContainer = ComicColorTokens.LightTextPrimary,
    secondary = ComicColorTokens.LightSakura,
    onSecondary = ComicColorTokens.LightTextPrimary,
    background = ComicColorTokens.LightBackground,
    onBackground = ComicColorTokens.LightTextPrimary,
    surface = ComicColorTokens.LightSurface,
    onSurface = ComicColorTokens.LightTextPrimary,
    surfaceVariant = ComicColorTokens.LightSurfaceVariant,
    onSurfaceVariant = ComicColorTokens.LightTextSecondary,
    outline = ComicColorTokens.LightBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = ComicColorTokens.DarkPrimary,
    onPrimary = ComicColorTokens.DarkOnPrimary,
    primaryContainer = ComicColorTokens.DarkSakura,
    onPrimaryContainer = ComicColorTokens.DarkTextPrimary,
    secondary = ComicColorTokens.DarkSakura,
    onSecondary = ComicColorTokens.DarkTextPrimary,
    background = ComicColorTokens.DarkBackground,
    onBackground = ComicColorTokens.DarkTextPrimary,
    surface = ComicColorTokens.DarkSurface,
    onSurface = ComicColorTokens.DarkTextPrimary,
    surfaceVariant = ComicColorTokens.DarkSurfaceVariant,
    onSurfaceVariant = ComicColorTokens.DarkTextSecondary,
    outline = ComicColorTokens.DarkBorder
)

@Composable
fun ComicVerseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ComicShapes,
        content = content
    )
}
