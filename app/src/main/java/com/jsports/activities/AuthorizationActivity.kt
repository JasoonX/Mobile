package com.jsports.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jsports.R
import com.jsports.fragments.LoginFragment

class AuthorizationActivity : AppCompatActivity() {

    private val fTrans = supportFragmentManager.beginTransaction()

    private var loginFragment:LoginFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        loginFragment = LoginFragment()
        fTrans.add(R.id.fl_auth,loginFragment!!)
        fTrans.commit()
    }
}
