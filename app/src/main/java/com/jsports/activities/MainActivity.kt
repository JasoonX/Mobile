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
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.jsports.LocaleHelper
import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.responses.BooleanResponse
import com.jsports.fragments.main.HomeFragment
import com.jsports.fragments.main.ProfileFragment
import com.jsports.helpers.RetrofitCallback
import com.jsports.helpers.isAuthenticated
import com.jsports.helpers.restartActivity
import com.jsports.storage.SharedPrefManager
import okhttp3.ResponseBody


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener {

    private var drawerLayout: DrawerLayout? = null
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private var navigationView: NavigationView? = null
    private var tvLogout: TextView? = null
    private var lang:String? = null
    private var localeHelper: LocaleHelper = LocaleHelper()
    private var fTrans = supportFragmentManager.beginTransaction()

    private var fragmentHome:Fragment? = null
    private var fragmentProfile:Fragment? = null

    private var currentScreen:Fragment? = null

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

        fragmentHome = HomeFragment()
        currentScreen = fragmentHome

        fTrans.add(R.id.fl_main,fragmentHome!!).commit()

        lang = intent.getStringExtra("lang")

        initSpinner()

        drawerLayout = findViewById(R.id.activity_main)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        drawerLayout!!.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        navigationView = findViewById(R.id.nv)
        navigationView!!.setNavigationItemSelectedListener(this)

        tvLogout = findViewById(R.id.tv_logout)
        tvLogout!!.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (actionBarDrawerToggle!!.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item!!)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_profile -> {
                if(currentScreen != fragmentProfile){
                    if(fragmentProfile == null){
                        fragmentProfile = ProfileFragment()
                    }
                    currentScreen = fragmentProfile
                    replacePage(fragmentProfile!!)
                }
                drawerLayout!!.closeDrawers()
            }
            R.id.mi_home -> {
                if(currentScreen != fragmentHome){
                    currentScreen = fragmentHome
                    replacePage(fragmentHome!!)
                }
                drawerLayout!!.closeDrawers()
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
                    lang = LocaleHelper.languages[position]
                    localeHelper.setLocale(baseContext,lang)
                    restartActivity(baseContext)
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
    }

    private fun logout() {
        SharedPrefManager.getInstance(this).logout()
        RetrofitClient.getInstance(this).api.logout()
        val intent = Intent(this,AuthorizationActivity::class.java)
        intent.putExtra("lang",lang)
        startActivity(intent)
        finish()
    }

    private fun replacePage(fragment:Fragment){
        fTrans = supportFragmentManager.beginTransaction()
        fTrans.replace(R.id.fl_main,fragment).addToBackStack(null).commit()
    }
}
