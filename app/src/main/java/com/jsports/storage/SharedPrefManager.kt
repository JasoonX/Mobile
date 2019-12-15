package com.jsports.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences


class SharedPrefManager(private val mCtx: Context) {
    private val sharedPrefName = "my_shared_pref"
    private val languageKey = "language"
    private val tokenKey = "accessToken"

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mInstance: SharedPrefManager? = null

        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }
    }

    fun saveToken(token:String){
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(sharedPrefName,
            Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(tokenKey,token).apply()
    }

    fun saveLanguage(lang:String?){
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(sharedPrefName,
            Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(languageKey,lang).apply()
    }

    fun getLanguage(defaultLanguage:String?):String?{
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(sharedPrefName,
            Context.MODE_PRIVATE)
        return sharedPreferences.getString(languageKey,defaultLanguage)
    }

    fun getAuthToken(): String?{
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(sharedPrefName,
            Context.MODE_PRIVATE)
        var token:String? = sharedPreferences.getString(tokenKey,null)
        if(token != null){
            token = "Bearer $token"
        }
        return token
    }

    fun getToken(): String?{
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(sharedPrefName,
            Context.MODE_PRIVATE)
        return sharedPreferences.getString(tokenKey,null)
    }

    fun isLoggedIn():Boolean{
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(sharedPrefName,
            Context.MODE_PRIVATE)
        val token = sharedPreferences.getString(tokenKey,null)
        return token != null
    }

    fun logout(){
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(sharedPrefName,
            Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(tokenKey).apply()
    }

    fun clear(){
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(sharedPrefName,
            Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}
