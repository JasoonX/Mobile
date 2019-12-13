package com.jsports.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences


class SharedPrefManager(private val mCtx: Context) {
    private val SHARED_PREFF_NAME = "my_shared_preff"
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
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFF_NAME,
            Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(tokenKey,token).apply()
    }

    fun getToken(): String{
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFF_NAME,
            Context.MODE_PRIVATE)
        return "Bearer ${sharedPreferences.getString(tokenKey,null)!!}"
    }

    fun isLoggedIn():Boolean{
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFF_NAME,
            Context.MODE_PRIVATE)
        return sharedPreferences.getString(tokenKey,null)!= null
    }

    fun clear(){
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFF_NAME,
            Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}
