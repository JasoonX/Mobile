package com.jsports.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.jsports.storage.SharedPrefManager


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent:Intent = if(SharedPrefManager.getInstance(this).isLoggedIn()){
            Intent(this, MainActivity::class.java)
        }else{
            Intent(this, AuthorizationActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}
