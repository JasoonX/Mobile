package com.jsports

import android.content.Context
import com.jsports.storage.SharedPrefManager
import java.util.*


class LocaleHelper {

    companion object{
        val languages = listOf("uk", "en")
    }

    fun onAttach(context: Context?): Context? {
        val lang = getPersistedData(context!!, Locale.getDefault().language)
        return setLocale(context, lang)
    }

    fun getLanguage(context: Context): String? {
        return getPersistedData(context, Locale.getDefault().language)
    }

    fun setLocale(
        context: Context,
        language: String?
    ): Context {
        persist(context, language)
        return updateResources(context, language)
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

    private fun updateResources(
        context: Context,
        language: String?
    ): Context {
        val locale = Locale(language!!)
        Locale.setDefault(locale)
        val configuration =
            context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}