package com.jsports.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.adapters.UsersAdapter
import com.jsports.api.models.Page
import com.jsports.api.models.User
import com.jsports.helpers.getErrorMessageFromJSON
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersFragment : Fragment(), View.OnClickListener {

    private lateinit var loadingScreen: FrameLayout
    private var usersPage: Page<User>? = null
    private var currentPage = 0
    private lateinit var rvUsers: RecyclerView
    private lateinit var ivPrevious: ImageView
    private lateinit var ivNext: ImageView
    private lateinit var tvPage: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity!!.title = getString(R.string.jsports_users)
        val view = inflater.inflate(R.layout.fragment_users, container, false)

        loadingScreen = activity!!.findViewById(R.id.ls_main)
        loadingScreen.visibility = View.VISIBLE

        rvUsers = view.findViewById(R.id.rv_users)
        rvUsers.layoutManager = LinearLayoutManager(activity!!)

        ivPrevious = view.findViewById(R.id.iv_previous_users)
        ivPrevious.setOnClickListener(this)
        ivPrevious.visibility = View.GONE

        ivNext = view.findViewById(R.id.iv_next_users)
        ivNext.setOnClickListener(this)
        ivNext.visibility = View.GONE

        tvPage = view.findViewById(R.id.tv_users_page)
        tvPage.text = (currentPage + 1).toString()

        getUsersPage(currentPage)

        return view
    }

    private fun getUsersPage(page: Int) {
        loadingScreen.visibility = View.VISIBLE
        val call = RetrofitClient.getInstance(activity!!).api.getUserProfiles(page)

        call.enqueue(object : Callback<Page<User>> {
            override fun onFailure(call: Call<Page<User>>, t: Throwable) {
                loadingScreen.visibility = View.GONE
                Toasty.error(
                    activity!!,
                    t.message!!,
                    Toasty.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call<Page<User>>, response: Response<Page<User>>) {
                if (response.body() != null) {
                    usersPage = response.body()
                    if (usersPage!!.first) {
                        ivPrevious.visibility = View.GONE
                    } else {
                        ivPrevious.visibility = View.VISIBLE
                    }

                    if (usersPage!!.last) {
                        ivNext.visibility = View.GONE
                    } else {
                        ivNext.visibility = View.VISIBLE
                    }
                    setRecyclerViewUsersAdapter()
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

    private fun seeMore(username: String) {
        val fragment = ProfileFragment()
        val arguments = Bundle()
        arguments.putString(ProfileFragment.USERNAME_KEY, username)
        fragment.arguments = arguments
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.fl_main, fragment)
            .addToBackStack(null).commit()
    }

    private fun setRecyclerViewUsersAdapter() {
        val adapter = UsersAdapter(activity!!, usersPage!!.content, ::seeMore)
        rvUsers.adapter = adapter
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_previous_users -> {
                currentPage -= 1
                tvPage.text = (currentPage + 1).toString()
                getUsersPage(currentPage)
            }

            R.id.iv_next_users -> {
                currentPage += 1
                tvPage.text = (currentPage + 1).toString()
                getUsersPage(currentPage)
            }
        }
    }


}
