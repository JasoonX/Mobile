package com.jsports.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.jsports.LocaleHelper
import com.jsports.R
import com.jsports.fragments.auth.LoginFragment
import com.jsports.helpers.restartActivity


class AuthorizationActivity : AppCompatActivity() {

    private var fTrans = supportFragmentManager.beginTransaction()

    private var loginFragment: LoginFragment? = null

    private var lang:String? = null

    private var localeHelper:LocaleHelper = LocaleHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        lang = intent.getStringExtra("lang")

        loginFragment = LoginFragment()
        fTrans.add(R.id.fl_auth, loginFragment!!)
        fTrans.commit()
        findViewById<FrameLayout>(R.id.auth_loading_screen).visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.authorization_menu, menu)
        val item: MenuItem = menu.findItem(R.id.spin_languages)

        val adapter: ArrayAdapter<String> =
            ArrayAdapter(
                this,
                R.layout.spinner_item,
                LocaleHelper.languages
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinLanguages: Spinner = item.actionView as Spinner

        val current = LocaleHelper.languages.indexOf(lang)

        spinLanguages.adapter = adapter


        spinLanguages.setSelection(current)

        spinLanguages.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View,
                position: Int, id: Long
            ) {
                if (position != current) {
                    localeHelper.setLocale(baseContext,LocaleHelper.languages[position])
                    restartActivity(baseContext)
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        return true
    }
}
