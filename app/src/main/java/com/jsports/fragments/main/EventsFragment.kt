package com.jsports.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.adapters.EventsAdapter
import com.jsports.api.models.Page
import com.jsports.api.models.User
import com.jsports.api.models.responses.EventResponse
import com.jsports.helpers.getErrorMessageFromJSON
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsFragment : Fragment() {

    private lateinit var eventsPage: Page<EventResponse>
    private lateinit var loadingScreen: FrameLayout
    private var currentPage = 0
    private lateinit var sportsDisciplines: List<String>
    private lateinit var rvEvents:RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.title = getString(R.string.jsports_events)
        val view = inflater.inflate(R.layout.fragment_events, container, false)
        loadingScreen = activity!!.findViewById(R.id.ls_main)
        loadingScreen.visibility = View.VISIBLE
        rvEvents = view.findViewById(R.id.rv_events)
        rvEvents.layoutManager = LinearLayoutManager(activity!!)

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
                    sportsDisciplines =
                        response.body()!!.sports.map { sport -> sport.sportsDiscipline }
                    if(sportsDisciplines.isNotEmpty()){
                        getEventsPage(currentPage,sportsDisciplines[0])
                    }
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

    private fun getEventsPage(page: Int, sportsDiscipline: String) {
        val call = RetrofitClient.getInstance(activity!!).api.getEvents(page, sportsDiscipline)

        call.enqueue(object : Callback<Page<EventResponse>> {
            override fun onFailure(call: Call<Page<EventResponse>>, t: Throwable) {
                loadingScreen.visibility = View.GONE
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
                    eventsPage = response.body()!!
                    initEventsPageUI()
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

    private fun initEventsPageUI() {
        val eventsAdapter = EventsAdapter(activity!!,eventsPage.content)
        rvEvents.adapter = eventsAdapter
    }
}
