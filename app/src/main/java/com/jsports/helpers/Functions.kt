package com.jsports.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.jsports.api.RetrofitClient
import com.jsports.api.models.Sport
import com.jsports.api.models.responses.BooleanResponse
import com.jsports.storage.SharedPrefManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun getErrorMessageFromJSON(json:String):String{
    return JSONObject(json).getString("message")
}

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

fun restartActivity(baseContext:Context,activity: Activity){
    val intent = baseContext.packageManager
        .getLaunchIntentForPackage(baseContext.packageName)
    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    baseContext.startActivity(intent)
    activity.finish()
}

fun List<Sport>.contains(sportDiscipline:String):Boolean{
    for(sport: Sport in this){
        if(sport.sportsDiscipline == sportDiscipline){
            return true
        }
    }
    return false
}