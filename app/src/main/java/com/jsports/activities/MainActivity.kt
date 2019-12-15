package com.jsports.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.responses.BooleanResponse
import com.jsports.helpers.RetrofitCallback
import com.jsports.helpers.isAuthenticated
import com.jsports.storage.SharedPrefManager
import okhttp3.ResponseBody

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isAuthenticated(this,object : RetrofitCallback<BooleanResponse>{
            override fun onSuccess(value: BooleanResponse) {
                if(!value.result){
                    logout()
                }
            }

            override fun onServerError(error: ResponseBody) {
            }

            override fun onError(throwable: Throwable) {
            }

        })
        title = getString(R.string.jsports_home)
    }

    private fun logout(){
        SharedPrefManager.getInstance(this).clear()
        RetrofitClient.getInstance(this).api.logout()
    }
}
