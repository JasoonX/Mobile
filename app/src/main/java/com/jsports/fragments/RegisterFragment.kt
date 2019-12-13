package com.jsports.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction

import com.jsports.R
import com.jsports.api.responses.MessageResponse

class RegisterFragment : Fragment() {

    private var tvLogin: TextView? = null
    private var fTrans: FragmentTransaction? = null
    private var loginFragment:LoginFragment? = null


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
        fTrans = activity!!.supportFragmentManager.beginTransaction()
        loginFragment = LoginFragment()
        tvLogin!!.setOnClickListener {
            fTrans!!.replace(R.id.fl_auth, loginFragment!!).commit()
        }
    }
}
