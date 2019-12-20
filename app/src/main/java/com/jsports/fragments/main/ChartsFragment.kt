package com.jsports.fragments.main


import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.responses.EventResponse
import com.jsports.helpers.LocaleHelper
import com.jsports.helpers.getErrorMessageFromJSON
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*


class ChartsFragment : Fragment() {

    companion object {
        const val DISCIPLINES_KEY = "disciplines_key"
    }

    private val eventsData: MutableList<EventsData> = mutableListOf()
    private lateinit var sportDisciplines: List<String>
    private lateinit var loadingScreen: FrameLayout
    private lateinit var llCharts: LinearLayout
    private lateinit var flCharts: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity!!.title = getString(R.string.jsports_charts)
        val view = inflater.inflate(R.layout.fragment_charts, container, false)

        sportDisciplines = arguments!!.getStringArrayList(DISCIPLINES_KEY)!!.toList()
        loadingScreen = activity!!.findViewById(R.id.ls_main)

        llCharts = view.findViewById(R.id.ll_charts)
        flCharts = view.findViewById(R.id.fl_charts)

        getEvents(0)
        return view
    }

    private fun getEvents(index: Int) {
        if (index == sportDisciplines.size) {
            loadingScreen.visibility = View.GONE
            initCharts()
            return
        } else if (loadingScreen.visibility != View.VISIBLE) {
            loadingScreen.visibility = View.VISIBLE
        }
        val discipline = sportDisciplines[index]
        if (sportDisciplines.isNotEmpty()) {
            val call = RetrofitClient.getInstance(activity!!)
                .api.getEvents(discipline)

            call.enqueue(object : Callback<List<EventResponse>> {
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
                        eventsData.add(EventsData(discipline, response.body()!!))
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

    private fun initCharts() {
        if (!validateEventsData()) {
            val noData = TextView(activity!!)
            noData.text = getString(R.string.not_enough_chart_data)
            noData.textSize = 18F
            noData.setTextColor(ContextCompat.getColor(activity!!, android.R.color.black))
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {

                gravity = Gravity.CENTER
            }
            noData.layoutParams = params
            flCharts.addView(noData)
        } else {
            for (eventsDataItem in eventsData) {
                if (eventsDataItem.events.size >= 5) {
                    buildChart(eventsDataItem)
                }
            }
        }
    }

    private fun buildChart(eventsData: EventsData) {
        val tvDiscipline = TextView(activity!!)
        tvDiscipline.text = getString(
            LocaleHelper.disciplineStringResources[eventsData.sportsDiscipline] ?: error("")
        )
        tvDiscipline.textSize = 24F
        tvDiscipline.setTextColor(ContextCompat.getColor(activity!!, android.R.color.black))
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            bottomMargin = 8
        }
        tvDiscipline.layoutParams = params

        val sprintDataPoints: MutableList<DataPoint> = mutableListOf()
        val crossDataPoints: MutableList<DataPoint> = mutableListOf()

        var sprintX = 0.0
        var crossX = 0.0
        for (event in eventsData.events) {
            val speed = (event.result.distance / event.result.time).toDouble()
            if (event.result.distance <= 500) {
                sprintDataPoints.add(DataPoint(sprintX, speed))
                sprintX++
            } else {
                crossDataPoints.add(DataPoint(crossX, speed))
                crossX++
            }
        }

        llCharts.addView(tvDiscipline)

        val par = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            500
        )

        if (sprintDataPoints.size >= 5) {
            val gvSprintEvents = GraphView(activity!!)
            val series =
                LineGraphSeries(
                    sprintDataPoints.toTypedArray()
                )
            gvSprintEvents.layoutParams = par
            gvSprintEvents.viewport.isScrollable = true
            gvSprintEvents.viewport.isScalable = true

            gvSprintEvents.title = "Speed for distance < 500m"
            gvSprintEvents.addSeries(series)
            llCharts.addView(gvSprintEvents)
        }

        if(crossDataPoints.size >= 5){
            val gvCrossEvents = GraphView(activity!!)
            val series =
                LineGraphSeries(
                    crossDataPoints.toTypedArray()
                )
            gvCrossEvents.layoutParams = par

            gvCrossEvents.viewport.isScrollable = true
            gvCrossEvents.viewport.isScalable = true


            gvCrossEvents.title = "Speed for distance > 500m"
            gvCrossEvents.addSeries(series)
            llCharts.addView(gvCrossEvents)
        }
    }

    private fun validateEventsData(): Boolean {
        var isValid = false
        for (ev in eventsData) {
            if (ev.events.size >= 10) {
                isValid = true
                break
            }
        }
        if (eventsData.isEmpty()) {
            isValid = false
        }
        return isValid
    }

    private class EventsData(val sportsDiscipline: String, val events: List<EventResponse>)
}
