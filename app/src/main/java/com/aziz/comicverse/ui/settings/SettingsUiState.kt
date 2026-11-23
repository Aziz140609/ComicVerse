package com.aziz.comicverse.ui.settings

import com.aziz.comicverse.preferences.LanguageMode
import com.aziz.comicverse.preferences.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val languageMode: LanguageMode = LanguageMode.INDONESIAN
)
