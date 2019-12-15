package com.jsports.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.jsports.LocaleHelper
import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.responses.BooleanResponse
import com.jsports.helpers.RetrofitCallback
import com.jsports.helpers.isAuthenticated
import com.jsports.helpers.restartActivity
import com.jsports.storage.SharedPrefManager
import okhttp3.ResponseBody


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener {

    private var dl: DrawerLayout? = null
    private var t: ActionBarDrawerToggle? = null
    private var nv: NavigationView? = null
    private var tvLogout: TextView? = null
    private var lang:String? = null
    private var localeHelper: LocaleHelper = LocaleHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isAuthenticated(this, object : RetrofitCallback<BooleanResponse> {
            override fun onSuccess(value: BooleanResponse) {
                if (!value.result) {
                    logout()
                }
            }

            override fun onServerError(error: ResponseBody) {
            }

            override fun onError(throwable: Throwable) {
            }

        })
        title = getString(R.string.jsports_home)

        lang = intent.getStringExtra("lang")

        initSpinner()

        dl = findViewById(R.id.activity_main)
        t = ActionBarDrawerToggle(this, dl, R.string.open, R.string.close)

        dl!!.addDrawerListener(t!!)
        t!!.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        nv = findViewById(R.id.nv)
        nv!!.setNavigationItemSelectedListener(this)

        tvLogout = findViewById(R.id.tv_logout)
        tvLogout!!.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (t!!.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item!!)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> {

            }
            else -> return true
        }
        return true
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_logout -> logout()
        }
    }

    private fun initSpinner(){
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                LocaleHelper.languages
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinLanguages: Spinner = findViewById(R.id.spin_languages_main)

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
    }

    private fun logout() {
        SharedPrefManager.getInstance(this).clear()
        RetrofitClient.getInstance(this).api.logout()
        val intent = Intent(this,AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
