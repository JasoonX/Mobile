package com.jsports.helpers

import android.content.Context
import com.jsports.R
import com.jsports.storage.SharedPrefManager
import java.util.*


class LocaleHelper {

    companion object {
        val languages = listOf("uk", "en")
        val disciplineStringResources = mapOf(
            "SWIMMING" to R.string.swimming,
            "CYCLING" to R.string.cycling,
            "ROWING" to R.string.rowing,
            "SKIING" to R.string.skiing,
            "SKATING" to R.string.skating,
            "BIATHLON" to R.string.biathlon,
            "RUNNING" to R.string.running
        )
    }

    fun getLanguage(context: Context): String? {
        return getPersistedData(context, Locale.getDefault().language)
    }

    fun setLocale(
        context: Context,
        language: String?
    ) {
        persist(context, language)
        updateResources(context, language)
    }

    private fun getPersistedData(
        context: Context,
        defaultLanguage: String
    ): String? {
        return SharedPrefManager.getInstance(context).getLanguage(defaultLanguage)
    }

    private fun persist(context: Context, language: String?) {
        SharedPrefManager.getInstance(context).saveLanguage(language)
    }

    @Suppress("DEPRECATION")
    private fun updateResources(
        context: Context,
        language: String?
    ) {
        val locale = Locale(language!!)
        Locale.setDefault(locale)

        val configuration =
            context.resources.configuration

        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        context.resources.updateConfiguration(
            configuration,
            context.resources.displayMetrics
        )
    }
}