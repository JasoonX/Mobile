package com.jsports.fragments.main


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.jsports.helpers.LocaleHelper

import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.User
import com.jsports.helpers.getErrorMessageFromJSON
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {

    lateinit var user: User

    private lateinit var loadingScreen: FrameLayout

    private lateinit var tvUsernameLabel: TextView
    private lateinit var tvGenderLabel: TextView
    private lateinit var tvEmailLabel: TextView
    private lateinit var tvHeightLabel: TextView
    private lateinit var tvWeightLabel: TextView
    private lateinit var tvDateLabel: TextView
    private lateinit var tvCountryLabel: TextView
    private lateinit var tvSportsLabel: TextView

    private lateinit var tvUsername: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeight: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvCountry: TextView
    private lateinit var tvSports: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.title = getString(R.string.jsports_profile)
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        loadingScreen = activity!!.findViewById(R.id.ls_main)
        loadingScreen.visibility = View.VISIBLE

        tvUsernameLabel = view.findViewById(R.id.tv_username_label)
        tvGenderLabel = view.findViewById(R.id.tv_gender_label)
        tvEmailLabel = view.findViewById(R.id.tv_email_label)
        tvHeightLabel = view.findViewById(R.id.tv_height_label)
        tvWeightLabel = view.findViewById(R.id.tv_weight_label)
        tvDateLabel = view.findViewById(R.id.tv_date_label)
        tvCountryLabel = view.findViewById(R.id.tv_country_label)
        tvSportsLabel = view.findViewById(R.id.tv_sports_label)


        tvUsername = view.findViewById(R.id.tv_username)
        tvGender = view.findViewById(R.id.tv_gender)
        tvEmail = view.findViewById(R.id.tv_email)
        tvHeight = view.findViewById(R.id.tv_height)
        tvWeight = view.findViewById(R.id.tv_weight)
        tvDate = view.findViewById(R.id.tv_date)
        tvCountry = view.findViewById(R.id.tv_country)
        tvSports = view.findViewById(R.id.tv_sports)

        getUser()
        return view
    }

    private fun getUser() {
        val call = RetrofitClient.getInstance(activity!!).api.getCurrentUserProfile()

        call.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                loadingScreen.visibility = View.GONE
                Toasty.error(
                    activity!!,
                    t.message!!,
                    Toasty.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.body() != null) {
                    user = response.body()!!
                    setProfileData()
                } else {
                    Toasty.error(
                        activity!!,
                        getErrorMessageFromJSON(response.errorBody()!!.string()),
                        Toasty.LENGTH_LONG
                    ).show()
                }
                loadingScreen.visibility = View.GONE
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setProfileData() {
        tvUsernameLabel.text = "${getString(R.string.username)}:"
        tvGenderLabel.text = "${getString(R.string.gender)}:"
        tvEmailLabel.text = "${getString(R.string.email)}:"
        tvHeightLabel.text = "${getString(R.string.height)}:"
        tvWeightLabel.text = "${getString(R.string.weight)}:"
        tvDateLabel.text = "${getString(R.string.date_of_birth)}:"
        tvCountryLabel.text = "${getString(R.string.country)}:"
        tvSportsLabel.text = "${getString(R.string.sports)}:"

        tvUsername.text = user.username
        tvGender.text =
            if (user.gender == "MALE")
                getString(R.string.male)
            else
                getString(R.string.female)
        tvEmail.text = user.email
        val height = user.height
        val heightString = if (height == null) {
            getString(R.string.not_set)
        } else {
            "$height${getString(R.string.cm)}"
        }
        tvHeight.text = heightString
        val weight = user.weight
        val weightString = if (weight == null) {
            getString(R.string.not_set)
        } else {
            "$weight${getString(R.string.kg)}"
        }
        tvWeight.text = weightString

        tvDate.text = if (user.born == null)
            getString(R.string.not_set)
        else
            user.born


        tvCountry.text = if (user.country == null)
            getString(R.string.not_set)
        else
            user.country

        var sportsString = ""
        val sports = user.sports
        if (sports.isNotEmpty()) {
            for (sport in sports) {
                sportsString += "${getString(LocaleHelper.disciplineStringResources[sport.sportsDiscipline]!!)}, "
            }
        }else{
            sportsString = getString(R.string.not_set)
        }
        sportsString = sportsString.substring(0,sportsString.length-2)
        tvSports.text = sportsString
    }
}
