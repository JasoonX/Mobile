package com.jsports.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.jsports.helpers.LocaleHelper
import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.responses.BooleanResponse
import com.jsports.dialogs.SimpleDialog
import com.jsports.fragments.main.EventsFragment
import com.jsports.fragments.main.HomeFragment
import com.jsports.fragments.main.ProfileFragment
import com.jsports.fragments.main.UsersFragment
import com.jsports.helpers.RetrofitCallback
import com.jsports.helpers.isAuthenticated
import com.jsports.helpers.restartActivity
import com.jsports.storage.SharedPrefManager
import okhttp3.ResponseBody


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var llLogout: LinearLayout
    private var lang: String? = null
    private val localeHelper: LocaleHelper =
        LocaleHelper()
    private var fTrans = supportFragmentManager.beginTransaction()

    private lateinit var fragmentHome: HomeFragment
    private var fragmentProfile: ProfileFragment? = null
    private var fragmentEvents: EventsFragment? = null
    private var fragmentUsers: UsersFragment? = null

    lateinit var miProfileSettings:MenuItem

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

        fTrans.add(R.id.fl_main, fragmentHome).commit()

        lang = intent.getStringExtra("lang")

        initSpinner()

        drawerLayout = findViewById(R.id.activity_main)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        navigationView = findViewById(R.id.nv)
        navigationView.setNavigationItemSelectedListener(this)

        llLogout = findViewById(R.id.ll_logout)
        llLogout.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.mi_profile_settings ->{
                val user = fragmentProfile!!.user
                val intent = Intent(this,EditProfileActivity::class.java)
                intent.putExtra(EditProfileActivity.USER_KEY,user)
                startActivity(intent)
            }
        }

        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(
            item
        )
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_profile -> {
                fragmentProfile = ProfileFragment()
                replacePage(fragmentProfile!!)
                drawerLayout.closeDrawers()
            }
            R.id.mi_home -> {
                fragmentHome = HomeFragment()
                replacePage(fragmentHome)
                drawerLayout.closeDrawers()
            }

            R.id.mi_events -> {
                fragmentEvents = EventsFragment()
                replacePage(fragmentEvents!!)
                drawerLayout.closeDrawers()
            }

            R.id.mi_users -> {
                fragmentUsers = UsersFragment()
                replacePage(fragmentUsers!!)
                drawerLayout.closeDrawers()
            }
            else -> return true
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ll_logout -> logoutPressed()
        }
    }

    private fun initSpinner() {
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
                    localeHelper.setLocale(baseContext, lang)
                    restartActivity(baseContext)
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        miProfileSettings = menu!!.findItem(R.id.mi_profile_settings)
        miProfileSettings.isVisible = false
        return true
    }

    private fun logoutPressed(){
        val dialog = SimpleDialog(this,getString(R.string.logout_message),{
            logout()
        })

        dialog.show(supportFragmentManager,null)
    }

    private fun logout() {
        SharedPrefManager.getInstance(this).logout()
        RetrofitClient.getInstance(this).api.logout()
        val intent = Intent(this, AuthorizationActivity::class.java)
        intent.putExtra("lang", lang)
        startActivity(intent)
        finish()
    }

    private fun replacePage(fragment: Fragment) {
        miProfileSettings.isVisible = fragment == fragmentProfile
        fTrans = supportFragmentManager.beginTransaction()
        fTrans.replace(R.id.fl_main, fragment).addToBackStack(null).commit()
    }
}
