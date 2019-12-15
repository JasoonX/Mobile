package com.jsports.api.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.jsports.api.models.responses.SportStatisticsResponse
import com.jsports.fragments.SportStatsPageFragment


class SportStatsPagerAdapter(
    fm: FragmentManager?,
    private val sportStatisticsResponses: List<SportStatisticsResponse>
) :
    FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return SportStatsPageFragment.newInstance(sportStatisticsResponses[position])
    }

    override fun getCount(): Int {
        return sportStatisticsResponses.size
    }
}