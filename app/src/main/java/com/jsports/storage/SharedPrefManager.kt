package com.jsports.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.jsports.api.RetrofitClient
import com.jsports.api.responses.BooleanResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

    fun isLoggedIn():Boolean{
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFF_NAME,
            Context.MODE_PRIVATE)
        if(sharedPreferences.getString(tokenKey,null)!= null){
            val call = RetrofitClient.instance.api.isTokenValid()
            var isValid = false
            call.enqueue(object : Callback<BooleanResponse> {
                override fun onFailure(call: Call<BooleanResponse>, t: Throwable) {
                }

                override fun onResponse(call: Call<BooleanResponse>, response: Response<BooleanResponse>) {
                    if(response.body() != null) {
                        isValid = response.body()!!.isResult()
                    }
                }
            })
            return isValid
        }
        return false
    }

    fun clear(){
        val sharedPreferences:SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFF_NAME,
            Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}
