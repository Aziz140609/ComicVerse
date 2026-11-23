package com.aziz.comicverse.core.designsystem

import androidx.compose.ui.graphics.Color

// Japanese Editorial Color Palette
object ComicColorTokens {
    // Light Mode (Authentic Japanese Aesthetics: Washi Ivory, Deep Crimson Wine, Sumi Ink, Dusty Sakura Mauve)
    val LightBackground = Color(0xFFF9F5EE) // Warm authentic Washi rice paper
    val LightSurface = Color(0xFFFFFFFF) // Porcelain white
    val LightSurfaceVariant = Color(0xFFF3ECE4) // Warm linen washi
    val LightPrimary = Color(0xFF8B1E32) // Japanese Crimson Wine / Dark Cherry (Regal, mature, non-neon)
    val LightOnPrimary = Color(0xFFFFFFFF)
    val LightSakura = Color(0xFFDCAAB0) // Muted Dusty Rose / Sakura Mauve (Natural, subtle)
    val LightSakuraDeep = Color(0xFFA82E44) // Deep Sakura Cherry
    val LightTextPrimary = Color(0xFF22171B) // Sumi Charcoal Ink
    val LightTextSecondary = Color(0xFF6B585E) // Japanese Slate Gray
    val LightBorder = Color(0xFFEADBCE) // Delicate Washi Paper Border

    // Dark Mode
    val DarkBackground = Color(0xFF181619)
    val DarkSurface = Color(0xFF242125)
    val DarkSurfaceVariant = Color(0xFF2F2A30)
    val DarkPrimary = Color(0xFFD4737F)
    val DarkOnPrimary = Color(0xFF181619)
    val DarkSakura = Color(0xFF8A5964)
    val DarkTextPrimary = Color(0xFFF5F0EB)
    val DarkTextSecondary = Color(0xFFA69390)
    val DarkBorder = Color(0xFF3B3438)

    // Accent & Status
    val StatusOngoing = Color(0xFF4A7C59)
    val StatusCompleted = Color(0xFF4A6F9E)
    val StatusHiatus = Color(0xFFB8784C)
    val GoldStar = Color(0xFFE5A93C)
}
