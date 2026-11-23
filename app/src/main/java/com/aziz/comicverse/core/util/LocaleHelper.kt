package com.aziz.comicverse.core.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun Context.applyLocale(languageCode: String): Context {
    val locale = Locale.forLanguageTag(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}
