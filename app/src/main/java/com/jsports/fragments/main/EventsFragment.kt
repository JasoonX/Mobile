package com.jsports.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.Page
import com.jsports.api.models.User
import com.jsports.api.models.responses.EventResponse
import com.jsports.helpers.getErrorMessageFromJSON
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsFragment : Fragment() {

    private var eventsPage: Page<EventResponse>? = null
    private var loadingScreen: FrameLayout? = null
    private var currentPage = 0
    private var sportsDisciplines: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadingScreen = activity!!.findViewById(R.id.ls_main)
        loadingScreen!!.visibility = View.VISIBLE
        getUser()
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    private fun getUser() {
        val call = RetrofitClient.getInstance(activity!!).api.getCurrentUserProfile()

        call.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                loadingScreen!!.visibility = View.GONE
                Toasty.error(
                    activity!!,
                    t.message!!,
                    Toasty.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.body() != null) {
                    sportsDisciplines =
                        response.body()!!.sports.map { sport -> sport.sportsDiscipline }
                    if(sportsDisciplines!!.isNotEmpty()){
                        getEventsPage(currentPage,sportsDisciplines!![0])
                    }
                } else {
                    Toasty.error(
                        activity!!,
                        getErrorMessageFromJSON(response.errorBody()!!.string()),
                        Toasty.LENGTH_LONG
                    ).show()
                }
                loadingScreen!!.visibility = View.GONE
            }
        })
    }

    private fun getEventsPage(page: Int, sportsDiscipline: String) {
        val call = RetrofitClient.getInstance(activity!!).api.getEvents(page, sportsDiscipline)

        call.enqueue(object : Callback<Page<EventResponse>> {
            override fun onFailure(call: Call<Page<EventResponse>>, t: Throwable) {
                loadingScreen!!.visibility = View.GONE
                Toasty.error(
                    activity!!,
                    t.message!!,
                    Toasty.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<Page<EventResponse>>,
                response: Response<Page<EventResponse>>
            ) {
                if (response.body() != null) {
                    eventsPage = response.body()
                    initEventsPageUI()
                } else {
                    Toasty.error(
                        activity!!,
                        getErrorMessageFromJSON(response.errorBody()!!.string()),
                        Toasty.LENGTH_LONG
                    ).show()
                }
                loadingScreen!!.visibility = View.GONE
            }
        })
    }

    private fun initEventsPageUI() {

    }
}
