package com.jsports.fragments.auth


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction

import com.jsports.R
import com.jsports.fragments.auth.LoginFragment

class RegisterFragment : Fragment(), View.OnClickListener {

    private var tvLogin: TextView? = null
    private var fTrans: FragmentTransaction? = null
    private var loginFragment: LoginFragment? = null
    private var loadingScreen: FrameLayout? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.title = getString(R.string.jsports_register)

        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLogin = view.findViewById(R.id.tv_login)
        tvLogin!!.setOnClickListener(this)

        loadingScreen = activity!!.findViewById(R.id.auth_loading_screen)

        fTrans = activity!!.supportFragmentManager.beginTransaction()
        loginFragment = LoginFragment()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_login -> fTrans!!.replace(
                R.id.fl_auth,
                loginFragment!!
            ).addToBackStack(null).commit()
        }
    }
}
