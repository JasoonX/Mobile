package com.jsports.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction

import com.jsports.R

class LoginFragment : Fragment() {

    private var tvRegister: TextView? = null
    private var fTrans: FragmentTransaction? = null
    private var registerFragment:RegisterFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvRegister = view.findViewById(R.id.tv_register)
        fTrans = activity!!.supportFragmentManager.beginTransaction()
        registerFragment = RegisterFragment()
        tvRegister!!.setOnClickListener {
            fTrans!!.replace(R.id.fl_auth, registerFragment!!).commit()
        }
    }
}
