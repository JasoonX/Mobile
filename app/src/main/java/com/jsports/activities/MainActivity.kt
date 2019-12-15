package com.jsports.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.responses.BooleanResponse
import com.jsports.helpers.RetrofitCallback
import com.jsports.helpers.isAuthenticated
import com.jsports.storage.SharedPrefManager
import okhttp3.ResponseBody


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var dl: DrawerLayout? = null
    private var t: ActionBarDrawerToggle? = null
    private var nv: NavigationView? = null

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

        dl = findViewById(R.id.activity_main)
        t = ActionBarDrawerToggle(this, dl, R.string.open, R.string.close)

        dl!!.addDrawerListener(t!!)
        t!!.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        nv = findViewById(R.id.nv)

        nv!!.setNavigationItemSelectedListener(this)
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

    private fun logout() {
        SharedPrefManager.getInstance(this).clear()
        RetrofitClient.getInstance(this).api.logout()
    }
}
