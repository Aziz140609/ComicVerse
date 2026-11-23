package com.aziz.comicverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.aziz.comicverse.core.util.applyLocale
import com.aziz.comicverse.navigation.ComicVerseNavGraph
import com.aziz.comicverse.preferences.LanguageMode
import com.aziz.comicverse.preferences.ThemeMode
import com.aziz.comicverse.preferences.UserPreferencesRepository
import com.aziz.comicverse.ui.theme.ComicVerseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesRepository = UserPreferencesRepository(this)

        setContent {
            val themeMode by preferencesRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val languageMode by preferencesRepository.languageMode.collectAsState(initial = LanguageMode.INDONESIAN)

            val isDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val currentContext = LocalContext.current
            val localizedContext = remember(languageMode) {
                currentContext.applyLocale(languageMode.code)
            }

            CompositionLocalProvider(LocalContext provides localizedContext) {
                ComicVerseTheme(darkTheme = isDarkTheme) {
                    ComicVerseNavGraph()
                }
            }
        }
    }
}
