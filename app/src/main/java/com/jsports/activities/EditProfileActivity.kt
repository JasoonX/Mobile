package com.jsports.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.androidbuts.multispinnerfilter.MultiSpinner
import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.Sport
import com.jsports.api.models.User
import com.jsports.api.models.requests.EditProfileRequest
import com.jsports.api.models.responses.MessageResponse
import com.jsports.dialogs.SimpleDialog
import com.jsports.helpers.CustomRegex
import com.jsports.helpers.LocaleHelper
import com.jsports.helpers.contains
import com.jsports.helpers.getErrorMessageFromJSON
import com.jsports.storage.SharedPrefManager
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.*
import kotlin.collections.LinkedHashMap


class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val USER_KEY = "user"
    }

    private lateinit var original: User
    private var sportsDisciplines: LinkedHashMap<String, Boolean> = LinkedHashMap()
    private var sportsDisciplinesStrings: MutableList<String> = mutableListOf()
    private lateinit var etFullName: EditText
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var etUsername: EditText
    private lateinit var etCountry: EditText
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var etDate: EditText
    private lateinit var btSubmit: Button
    private lateinit var sportsSpinner: MultiSpinner
    private lateinit var loadingScreen: FrameLayout
    private var selectedSports: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = "JSports - ${getString(R.string.profile_settings)}"

        original = intent.getSerializableExtra(USER_KEY) as User

        etFullName = findViewById(R.id.et_profile_fullname)
        rbMale = findViewById(R.id.rb_profile_gender_male)
        rbFemale = findViewById(R.id.rb_profile_gender_female)
        etUsername = findViewById(R.id.et_profile_username)
        etCountry = findViewById(R.id.et_profile_country)
        etHeight = findViewById(R.id.et_profile_height)
        etWeight = findViewById(R.id.et_profile_weight)
        etDate = findViewById(R.id.et_profile_date)

        loadingScreen = findViewById(R.id.ls_edit_profile)
        loadingScreen.visibility = View.GONE

        sportsSpinner = findViewById(R.id.spin_edit_profile)
        for ((k, v) in LocaleHelper.disciplineStringResources) {
            sportsDisciplines[getString(v)] = original.sports.contains(k)
            sportsDisciplinesStrings.add(k)
        }
        initSportsSpinner()

        btSubmit = findViewById(R.id.bt_edit_profile)
        btSubmit.setOnClickListener(this)

        initOriginalData()
    }

    private fun initSportsSpinner() {
        sportsSpinner.setItems(
            sportsDisciplines
        ) { selected ->
            val selSports = mutableListOf<String>()
            for (i in selected.indices) {
                if (selected[i]) {
                    selSports.add(sportsDisciplinesStrings[i])
                }
            }
            selectedSports = selSports
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun initOriginalData() {
        etFullName.setText(original.fullname)
        if (original.gender == "MALE") {
            rbMale.isChecked = true
            rbFemale.isChecked = false
        } else {
            rbMale.isChecked = false
            rbFemale.isChecked = true
        }
        etUsername.setText(original.username)
        etCountry.setText(original.country)
        etHeight.setText(original.height.toString())
        etWeight.setText(original.weight.toString())
        etDate.setText(original.born)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.bt_edit_profile -> editProfilePressed()
        }
    }

    private fun editProfilePressed() {
        val dialog = SimpleDialog(this, getString(R.string.change_profile_message), {
            editProfile()
        })

        dialog.show(supportFragmentManager, null)
    }

    private fun editProfile() {
        loadingScreen.visibility = View.VISIBLE
        val fullName =
            if (etFullName.text.toString() != original.fullname)
                etFullName.text.toString()
            else null

        val gender =
            if (rbMale.isChecked && original.gender != "MALE")
                "FEMALE"
            else {
                "MALE"
            }
        val username =
            if (etUsername.text.toString() != original.username)
                etUsername.text.toString()
            else null

        val country =
            if (etCountry.text.toString() != original.country)
                etCountry.text.toString()
            else null

        val height =
            if (etHeight.text.toString().toFloat() != original.height) {
                etHeight.text.toString().toFloat()
            } else null

        val weight =
            if (etWeight.text.toString().toFloat() != original.weight) {
                etWeight.text.toString().toFloat()
            } else null

        val date =
            if (etDate.text.toString() != original.born)
                etDate.text.toString()
            else null

        val sports: MutableList<String> = mutableListOf()
        for (sport in original.sports) {
            if (selectedSports.contains(sport.sportsDiscipline)) {
                sports.add(sport.sportsDiscipline)
                selectedSports.remove(sport.sportsDiscipline)
            }
        }
        if (selectedSports.isNotEmpty()) {
            sports.addAll(selectedSports)
        }

        val request = EditProfileRequest(
            fullName, gender, username, height, weight, date, country, sports
        )

        if (validateEditRequest(request)) {
            val call = RetrofitClient.getInstance(this).api.updateProfile(request)

            call.enqueue(object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    loadingScreen.visibility = View.GONE
                    Toasty.error(
                        this@EditProfileActivity,
                        t.message!!,
                        Toasty.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.body() != null) {
                        Toasty.success(
                            this@EditProfileActivity,
                            response.body()!!.message,
                            Toasty.LENGTH_LONG
                        ).show()
                    } else {
                        Toasty.error(
                            this@EditProfileActivity,
                            getErrorMessageFromJSON(response.errorBody()!!.string()),
                            Toasty.LENGTH_LONG
                        ).show()
                    }
                    loadingScreen.visibility = View.GONE
                }

            })
        } else {
            loadingScreen.visibility = View.GONE
        }
    }

    private fun validateEditRequest(editProfileRequest: EditProfileRequest): Boolean {
        val usernameRegex = Regex(CustomRegex.USERNAME)
        val fullnameRegex = Regex(CustomRegex.FULL_NAME)
        val dateRegex = Regex(CustomRegex.DATE)

        val countryCodes = Locale.getISOCountries()

        val countries: MutableSet<String> = mutableSetOf()
        for (countryCode in countryCodes) {
            val obj = Locale("", countryCode)
            countries.add(obj.displayCountry)
        }

        val current = LocalDate.now()
        val given =
            if (editProfileRequest.born != null) LocalDate.parse(editProfileRequest.born) else null

        when {

            editProfileRequest.fullname != null && editProfileRequest.fullname.isEmpty() -> {
                etFullName.error = getString(R.string.ful_name_required)
                return false
            }

            editProfileRequest.fullname != null && editProfileRequest.fullname.length > 50 -> {
                etFullName.error = getString(R.string.full_name_long)
                return false
            }

            editProfileRequest.fullname != null && editProfileRequest.fullname.length < 5 -> {
                etFullName.error = getString(R.string.full_name_short)
                return false
            }

            editProfileRequest.fullname != null && !fullnameRegex.matches(editProfileRequest.fullname) -> {
                etFullName.error = getString(R.string.wrong_full_name)
                return false
            }

            editProfileRequest.username != null && editProfileRequest.username.isEmpty() -> {
                etUsername.error = getString(R.string.username_required)
                return false
            }
            editProfileRequest.username != null && editProfileRequest.username.length < 4 -> {
                etUsername.error = getString(R.string.username_short)
                return false
            }
            editProfileRequest.username != null && editProfileRequest.username.length > 30 -> {
                etUsername.error = getString(R.string.username_long)
                return false
            }
            editProfileRequest.username != null && !usernameRegex.matches(editProfileRequest.username) -> {
                etUsername.error = getString(R.string.username_wrong)
                return false
            }

            editProfileRequest.country != null && !countries.contains(editProfileRequest.country) -> {
                etCountry.error = getString(R.string.country_not_found)
                return false
            }

            editProfileRequest.height != null && editProfileRequest.height > 250 -> {
                etHeight.error = getString(R.string.height_large)
                return false
            }

            editProfileRequest.height != null && editProfileRequest.height < 56 -> {
                etHeight.error = getString(R.string.height_small)
                return false
            }

            editProfileRequest.weight != null && editProfileRequest.weight > 200 -> {
                etWeight.error = getString(R.string.weight_large)
                return false
            }

            editProfileRequest.weight != null && editProfileRequest.weight < 20 -> {
                etWeight.error = getString(R.string.weight_small)
                return false
            }

            editProfileRequest.born != null && !dateRegex.matches(editProfileRequest.born) -> {
                etDate.error = getString(R.string.date_format_wrong)
                return false
            }

            editProfileRequest.born != null && current.minusYears(4).isBefore(given) -> {
                etDate.error = getString(R.string.age_small)
                return false
            }

            editProfileRequest.born != null && current.minusYears(100).isAfter(given) -> {
                etDate.error = getString(R.string.age_big)
                return false
            }
        }
        return true
    }
}
