package com.jsports.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jsports.LocaleHelper
import com.jsports.R
import com.jsports.api.models.responses.SportStatisticsResponse


class SportStatsPageFragment(private val sportStatisticsResponse: SportStatisticsResponse) :
    Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_sport_stats_page, container, false)
        val tvDiscipline = view.findViewById<TextView>(R.id.tv_discipline)
        val tvPeopleNum = view.findViewById<TextView>(R.id.tv_people_num)
        val tvMaleNum = view.findViewById<TextView>(R.id.tv_male_num)
        val tvFemaleNum = view.findViewById<TextView>(R.id.tv_female_num)
        val tvEventsNum = view.findViewById<TextView>(R.id.tv_events_num)
        val tvYourEventsNum = view.findViewById<TextView>(R.id.tv_your_events_num)
        val tvYourEventsPercent = view.findViewById<TextView>(R.id.tv_your_events_percent)

        tvDiscipline.text =
            getString(LocaleHelper.disciplineStringResources[sportStatisticsResponse.discipline]!!)

        tvPeopleNum.text = sportStatisticsResponse.people.toString()
        tvMaleNum.text = sportStatisticsResponse.males.toString()
        tvFemaleNum.text = sportStatisticsResponse.females.toString()
        tvEventsNum.text = sportStatisticsResponse.eventsCount.toString()
        tvYourEventsNum.text = sportStatisticsResponse.userEventsCount.toString()

        var percen:Double = sportStatisticsResponse.userEventsPercent
        if(percen.isNaN()){
            percen = 0.0
        }
        tvYourEventsPercent.text =
            String.format(
                getString(R.string.percent),
                String.format("%.2f", percen)
            )
        return view
    }

    companion object {

        fun newInstance(sportStatisticsResponse: SportStatisticsResponse): SportStatsPageFragment {
            return SportStatsPageFragment(sportStatisticsResponse)
        }
    }
}
