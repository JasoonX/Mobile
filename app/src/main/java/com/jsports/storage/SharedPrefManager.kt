package com.jsports.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences


class SharedPrefManager(private val mCtx: Context) {
    private val SHARED_PREFF_NAME = "my_shared_preff"

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

    fun isLoggedIn():Boolean{
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFF_NAME,
            Context.MODE_PRIVATE)
        return sharedPreferences.getString("login",null)!= null
    }

    fun clear(){
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFF_NAME,
            Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}
