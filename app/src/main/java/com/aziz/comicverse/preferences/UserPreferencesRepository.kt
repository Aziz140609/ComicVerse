package com.aziz.comicverse.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class LanguageMode(val code: String) {
    INDONESIAN("in"),
    ENGLISH("en")
}

class UserPreferencesRepository(
    private val context: Context
) {
    private val themeKey = stringPreferencesKey("theme_mode")
    private val languageKey = stringPreferencesKey("language_mode")

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        when (preferences[themeKey]) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            ThemeMode.DARK.name -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    val languageMode: Flow<LanguageMode> = context.dataStore.data.map { preferences ->
        when (preferences[languageKey]) {
            LanguageMode.ENGLISH.name -> LanguageMode.ENGLISH
            else -> LanguageMode.INDONESIAN
        }
    }

    suspend fun getLanguageMode(): LanguageMode {
        return try {
            languageMode.first()
        } catch (e: Exception) {
            LanguageMode.INDONESIAN
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = mode.name
        }
    }

    suspend fun setLanguageMode(mode: LanguageMode) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = mode.name
        }
    }
}
