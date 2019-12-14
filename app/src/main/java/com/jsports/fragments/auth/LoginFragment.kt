package com.jsports.fragments.auth


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction

import com.jsports.R
import com.jsports.activities.MainActivity
import com.jsports.api.RetrofitClient
import com.jsports.api.requests.LoginRequest
import com.jsports.api.responses.LoginResponse
import com.jsports.storage.SharedPrefManager
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_register -> swapFragmentToRegister()
            R.id.bt_login -> login()
            R.id.tv_forgot_pass -> {}
        }
    }

    private var tvRegister: TextView? = null
    private var fTrans: FragmentTransaction? = null
    private var registerFragment: RegisterFragment? = null
    private var etUsername: EditText? = null
    private var etPassword: EditText? = null
    private var btLogin: Button? = null
    private var tvForgotPass: TextView? = null
    private var loadingScreen:FrameLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.title = getString(R.string.jsports_login)
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvRegister = view.findViewById(R.id.tv_register)
        tvRegister!!.setOnClickListener(this)

        etUsername = view.findViewById(R.id.et_login_username)
        etPassword = view.findViewById(R.id.et_login_password)

        btLogin = view.findViewById(R.id.bt_login)
        btLogin!!.setOnClickListener(this)

        tvForgotPass = view.findViewById(R.id.tv_forgot_pass)
        tvForgotPass!!.setOnClickListener(this)

        loadingScreen = activity!!.findViewById(R.id.auth_loading_screen)

        fTrans = activity!!.supportFragmentManager.beginTransaction()
        registerFragment = RegisterFragment()
    }

    private fun swapFragmentToRegister() {
        fTrans!!.replace(R.id.fl_auth, registerFragment!!).addToBackStack(null).commit()
    }

    private fun login() {
        val username = etUsername!!.text.toString()
        val password = etPassword!!.text.toString()

        if (validateCredentials(username, password)) {
            loadingScreen!!.visibility = View.VISIBLE
            val request = LoginRequest(username,password)
            val call = RetrofitClient.instance.api.login(request)
            call.enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loadingScreen!!.visibility = View.GONE
                    Toasty.error(
                        activity!!,
                        t.message!!,
                        Toasty.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.body() != null) {
                        SharedPrefManager.getInstance(activity!!).saveToken(response.body()!!.token)
                        val intent = Intent(activity!!,MainActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                    } else {
                        Toasty.error(
                            activity!!,
                            JSONObject(response.errorBody()!!.string()).getString("message"),
                            Toasty.LENGTH_LONG
                        ).show()
                    }
                    loadingScreen!!.visibility = View.GONE
                }

            })
        }
    }

    private fun validateCredentials(username: String, password: String): Boolean {

        val usernameRegex = Regex("""^[a-z0-9_-]{3,16}${'$'}""")

        when {
            username.isEmpty() -> {
                etUsername!!.error = getString(R.string.username_required)
                return false
            }
            username.length < 4 -> {
                etUsername!!.error = getString(R.string.username_short)
                return false
            }
            username.length > 30 -> {
                etUsername!!.error = getString(R.string.username_long)
                return false
            }
            !usernameRegex.matches(username) -> {
                etUsername!!.error = getString(R.string.username_wrong)
                return false
            }
        }

        when {
            password.isEmpty() -> {
                etPassword!!.error = getString(R.string.password_required)
                return false
            }
        }
        return true
    }
}
