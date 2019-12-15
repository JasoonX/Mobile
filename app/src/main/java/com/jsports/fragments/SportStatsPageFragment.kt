package com.jsports.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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
        tvDiscipline.text = sportStatisticsResponse.discipline
        return view
    }

    companion object {

        fun newInstance(sportStatisticsResponse: SportStatisticsResponse): SportStatsPageFragment {
            val pageFragment = SportStatsPageFragment(sportStatisticsResponse)
            val arguments = Bundle()

            pageFragment.arguments = arguments
            return pageFragment
        }
    }
}
