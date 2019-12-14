package com.jsports.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.jsports.R
import com.jsports.fragments.auth.LoginFragment

class AuthorizationActivity : AppCompatActivity() {

    private val fTrans = supportFragmentManager.beginTransaction()

    private var loginFragment: LoginFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        loginFragment = LoginFragment()
        fTrans.add(R.id.fl_auth,loginFragment!!)
        fTrans.commit()
        findViewById<FrameLayout>(R.id.auth_loading_screen).visibility = View.GONE
    }
}
