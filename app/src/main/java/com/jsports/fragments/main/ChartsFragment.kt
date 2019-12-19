package com.jsports.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.responses.EventResponse
import com.jsports.helpers.getErrorMessageFromJSON
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChartsFragment : Fragment() {

    companion object {
        const val DISCIPLINES_KEY = "disciplines_key"
    }

    private val eventsData: MutableList<EventsData> = mutableListOf()
    private lateinit var sportDisciplines: List<String>
    private lateinit var loadingScreen: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity!!.title = getString(R.string.jsports_charts)
        val view = inflater.inflate(R.layout.fragment_charts, container, false)

        sportDisciplines = arguments!!.getStringArrayList(DISCIPLINES_KEY)!!.toList()
        loadingScreen = activity!!.findViewById(R.id.ls_main)

        getEvents(0)
        return view
    }

    private fun getEvents(index:Int) {
        if(index == sportDisciplines.size){
            loadingScreen.visibility = View.GONE
            initCharts()
            return
        }else if(loadingScreen.visibility != View.VISIBLE){
            loadingScreen.visibility = View.VISIBLE
        }
        val discipline = sportDisciplines[index]
        if (sportDisciplines.isNotEmpty()) {
            val call = RetrofitClient.getInstance(activity!!)
                .api.getEvents(discipline)

            call.enqueue(object : Callback<List<EventResponse>>{
                override fun onFailure(call: Call<List<EventResponse>>, t: Throwable) {
                    loadingScreen.visibility = View.GONE
                    Toasty.error(
                        activity!!,
                        t.message!!,
                        Toasty.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<List<EventResponse>>,
                    response: Response<List<EventResponse>>
                ) {
                    if (response.body() != null) {
                        eventsData.add(EventsData(discipline,response.body()!!))
                        getEvents(index + 1)
                    } else {
                        Toasty.error(
                            activity!!,
                            getErrorMessageFromJSON(response.errorBody()!!.string()),
                            Toasty.LENGTH_LONG
                        ).show()
                    }
                }

            })
        }
    }

    private fun initCharts(){

    }

    private class EventsData(val sportsDiscipline:String,val events:List<EventResponse>)
}
