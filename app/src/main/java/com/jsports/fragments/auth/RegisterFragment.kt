package com.jsports.fragments.auth


import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.requests.RegisterRequest
import com.jsports.api.models.responses.MessageResponse
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.*
import java.util.regex.Pattern


class RegisterFragment : Fragment(), View.OnClickListener {

    private var tvLogin: TextView? = null
    private var fTrans: FragmentTransaction? = null
    private var loginFragment: LoginFragment? = null
    private var loadingScreen: FrameLayout? = null
    private var etFullName: EditText? = null
    private var rbMale: RadioButton? = null
    private var etUsername: EditText? = null
    private var etPassword: EditText? = null
    private var etConfirmPassword: EditText? = null
    private var etEmail: EditText? = null
    private var etCountry: EditText? = null
    private var etHeight: EditText? = null
    private var etWeight: EditText? = null
    private var etDateOfBirth: EditText? = null
    private var btSubmit: Button? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.title = getString(R.string.jsports_register)
        loadingScreen = activity!!.findViewById(R.id.auth_loading_screen)

        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLogin = view.findViewById(R.id.tv_login)
        tvLogin!!.setOnClickListener(this)

        etFullName = view.findViewById(R.id.et_register_fullname)
        rbMale = view.findViewById(R.id.rb_gender_male)
        etUsername = view.findViewById(R.id.et_register_username)
        etPassword = view.findViewById(R.id.et_register_password)
        etConfirmPassword = view.findViewById(R.id.et_register_confirm_password)
        etEmail = view.findViewById(R.id.et_register_email)
        etCountry = view.findViewById(R.id.et_register_country)
        etHeight = view.findViewById(R.id.et_register_height)
        etWeight = view.findViewById(R.id.et_register_weight)
        etDateOfBirth = view.findViewById(R.id.et_register_date)

        btSubmit = view.findViewById(R.id.bt_register)
        btSubmit!!.setOnClickListener(this)

        fTrans = activity!!.supportFragmentManager.beginTransaction()
        loginFragment = LoginFragment()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_login -> fTrans!!.replace(
                R.id.fl_auth,
                loginFragment!!
            ).addToBackStack(null).commit()

            R.id.bt_register -> register()
        }
    }

    private fun register() {
        val fullName: String = etFullName!!.text.toString()
        val gender: String = if (rbMale!!.isChecked) "MALE" else "FEMALE"
        val username: String = etUsername!!.text.toString()
        val password: String = etPassword!!.text.toString()
        val email: String = etEmail!!.text.toString()
        val country: String? =
            if (etCountry!!.text.toString().isEmpty()) null else etCountry!!.text.toString()

        val height: Float? =
            if (etHeight!!.text.toString().isEmpty()) null else etHeight!!.text.toString().toFloat()

        val weight: Float? =
            if (etWeight!!.text.toString().isEmpty()) null else etWeight!!.text.toString().toFloat()

        val date: String? =
            if (etDateOfBirth!!.text.toString().isEmpty()) null else etDateOfBirth!!.text.toString()

        val registerRequest = RegisterRequest(
            fullName,
            gender,
            username,
            email,
            password,
            height,
            weight,
            date,
            country
        )

        if (validateRegisterRequest(registerRequest)) {
            loadingScreen!!.visibility = View.VISIBLE
            val call = RetrofitClient.getInstance(activity!!).api.register(registerRequest)

            call.enqueue(object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    loadingScreen!!.visibility = View.GONE
                    Toasty.error(
                        activity!!,
                        t.message!!,
                        Toasty.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.body() != null) {
                        Toasty.success(activity!!, response.body()!!.message, Toasty.LENGTH_LONG)
                            .show()
                        activity!!.supportFragmentManager.popBackStack()
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


    private fun validateRegisterRequest(registerRequest: RegisterRequest): Boolean {
        val usernameRegex = Regex("""^[a-z0-9_-]{3,16}$""")
        val dateRegex = Regex("""^([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))$""")

        val countryCodes = Locale.getISOCountries()

        val countries: MutableSet<String> = mutableSetOf()
        for (countryCode in countryCodes) {
            val obj = Locale("", countryCode)
            countries.add(obj.displayCountry)
        }

        val current = LocalDate.now()
        val given =
            if (registerRequest.born != null) LocalDate.parse(registerRequest.born) else null

        when {
            registerRequest.fullname.isEmpty() -> {
                etFullName!!.error = getString(R.string.ful_name_required)
                return false
            }

            registerRequest.fullname.length > 50 -> {
                etFullName!!.error = getString(R.string.full_name_long)
                return false
            }

            registerRequest.fullname.length < 5 -> {
                etFullName!!.error = getString(R.string.full_name_short)
                return false
            }

            registerRequest.username.isEmpty() -> {
                etUsername!!.error = getString(R.string.username_required)
                return false
            }
            registerRequest.username.length < 4 -> {
                etUsername!!.error = getString(R.string.username_short)
                return false
            }
            registerRequest.username.length > 30 -> {
                etUsername!!.error = getString(R.string.username_long)
                return false
            }
            !usernameRegex.matches(registerRequest.username) -> {
                etUsername!!.error = getString(R.string.username_wrong)
                return false
            }

            registerRequest.password.isEmpty() -> {
                etPassword!!.error = getString(R.string.password_required)
                return false
            }

            registerRequest.password.length < 10 -> {
                etPassword!!.error = getString(R.string.password_short)
                return false
            }

            registerRequest.password != etConfirmPassword!!.text.toString() -> {
                etConfirmPassword!!.error = getString(R.string.passwords_not_match)
                return false
            }

            registerRequest.email.isEmpty() -> {
                etEmail!!.error = getString(R.string.email_required)
                return false
            }

            !Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), registerRequest.email) -> {
                etEmail!!.error = getString(R.string.wrong_email)
                return false
            }

            registerRequest.country != null && !countries.contains(registerRequest.country) -> {
                etCountry!!.error = getString(R.string.country_not_found)
                return false
            }

            registerRequest.height != null && registerRequest.height > 250 -> {
                etHeight!!.error = getString(R.string.height_large)
                return false
            }

            registerRequest.height != null && registerRequest.height < 56 -> {
                etHeight!!.error = getString(R.string.height_small)
                return false
            }

            registerRequest.weight != null && registerRequest.weight > 200 -> {
                etWeight!!.error = getString(R.string.weight_large)
                return false
            }

            registerRequest.weight != null && registerRequest.weight < 20 -> {
                etWeight!!.error = getString(R.string.weight_small)
                return false
            }

            registerRequest.born != null && !dateRegex.matches(registerRequest.born) -> {
                etDateOfBirth!!.error = getString(R.string.date_format_wrong)
                return false
            }

            registerRequest.born != null && current.minusYears(4).isBefore(given) -> {
                etDateOfBirth!!.error = getString(R.string.age_small)
                return false
            }

            registerRequest.born != null && current.minusYears(100).isAfter(given) -> {
                etDateOfBirth!!.error = getString(R.string.age_big)
                return false
            }
        }
        return true
    }
}
