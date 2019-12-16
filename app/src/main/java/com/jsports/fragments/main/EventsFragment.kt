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
import com.jsports.api.adapters.EventsAdapter
import com.jsports.api.models.Page
import com.jsports.api.models.User
import com.jsports.api.models.responses.EventResponse
import com.jsports.helpers.getErrorMessageFromJSON
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsFragment : Fragment(),View.OnClickListener {

    private lateinit var eventsPage: Page<EventResponse>
    private lateinit var loadingScreen: FrameLayout
    private var currentPage = 0
    private lateinit var currentSportsDiscipline:String
    private lateinit var sportsDisciplines: List<String>
    private lateinit var rvEvents:RecyclerView
    private lateinit var ivNextEventsPage:ImageView
    private lateinit var ivPreviousEventsPage:ImageView
    private lateinit var tvCurrentPage:TextView

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

        ivNextEventsPage = view.findViewById(R.id.iv_next_events)
        ivNextEventsPage.setOnClickListener(this)

        ivPreviousEventsPage = view.findViewById(R.id.iv_previous_events)
        ivPreviousEventsPage.setOnClickListener(this)

        tvCurrentPage = view.findViewById(R.id.tv_events_page)
        tvCurrentPage.text = (currentPage+1).toString()

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
                        currentSportsDiscipline = sportsDisciplines[0]
                        getEventsPage(currentPage,currentSportsDiscipline)
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
        loadingScreen.visibility = View.VISIBLE
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
                    if(eventsPage.first){
                        ivPreviousEventsPage.visibility = View.INVISIBLE
                    }else{
                        ivPreviousEventsPage.visibility = View.VISIBLE
                    }
                    if(eventsPage.last){
                        ivNextEventsPage.visibility = View.INVISIBLE
                    }else{
                        ivNextEventsPage.visibility = View.VISIBLE
                    }
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

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.iv_next_events -> {
                if(!eventsPage.last){
                    currentPage += 1
                    tvCurrentPage.text = (currentPage+1).toString()
                    getEventsPage(currentPage,currentSportsDiscipline)
                }
            }

            R.id.iv_previous_events -> {
                if(!eventsPage.first){
                    currentPage -= 1
                    tvCurrentPage.text = (currentPage+1).toString()
                    getEventsPage(currentPage,currentSportsDiscipline)
                }
            }
        }
    }
}
