package com.jsports.helpers

import android.content.Context
import com.jsports.api.RetrofitClient
import com.jsports.api.models.responses.BooleanResponse
import com.jsports.storage.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun isAuthenticated(context:Context, callback:RetrofitCallback<BooleanResponse>){
    val token = SharedPrefManager.getInstance(context).getToken()
    val call = RetrofitClient.getInstance(context).api.isTokenValid(token!!)
    call.enqueue(object : Callback<BooleanResponse>{
        override fun onFailure(call: Call<BooleanResponse>, t: Throwable) {
            callback.onError(t)
        }

        override fun onResponse(call: Call<BooleanResponse>, response: Response<BooleanResponse>) {
            if(response.body() != null){
                callback.onSuccess(response.body()!!)
            }else{
                callback.onServerError(response.errorBody()!!)
            }
        }

    })
}