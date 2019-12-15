package com.jsports.helpers

import android.content.Context
import com.jsports.api.RetrofitClient
import com.jsports.storage.SharedPrefManager

fun isAuthenticated(context:Context): Boolean{
    val token = SharedPrefManager.getInstance(context).getToken()
    val call = RetrofitClient.getInstance(context).api.isTokenValid(token)
    val response = call.execute()
    if(response.body() != null){
        return response.body()!!.result
    }
    return false
}