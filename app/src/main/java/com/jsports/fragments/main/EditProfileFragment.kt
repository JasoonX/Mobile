package com.jsports.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jsports.R

class EditProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.title = "JSports - ${getString(R.string.profile_settings)}"
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }


}
