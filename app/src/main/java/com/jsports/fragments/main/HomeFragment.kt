package com.jsports.fragments.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.adapters.SportStatsPagerAdapter
import com.jsports.api.models.responses.SportStatisticsResponse
import com.jsports.helpers.getErrorMessageFromJSON
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(), View.OnClickListener {

    private var sportStatsPager: ViewPager? = null
    private var pagerAdapter: PagerAdapter? = null
    private var loadingScreen: FrameLayout? = null
    private var sportStats: List<SportStatisticsResponse>? = null
    private var previous: ImageView? = null
    private var next: ImageView? = null
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        activity!!.title = getString(R.string.jsports_home)
        loadingScreen = activity!!.findViewById(R.id.ls_main)
        loadingScreen!!.visibility = View.VISIBLE
        sportStatsPager = view.findViewById(R.id.pager_sport_stats)

        sportStatsPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                onPageChanged(position)
            }

        })

        previous = view.findViewById(R.id.iv_previous)
        previous!!.setOnClickListener(this)
        previous!!.visibility = View.GONE

        next = view.findViewById(R.id.iv_next)
        next!!.setOnClickListener(this)

        getSportStatistics()
        return view
    }

    private fun onPageChanged(page: Int) {
        currentPage = page
        if (page == 0) {
            previous!!.visibility = View.GONE
        } else {
            previous!!.visibility = View.VISIBLE
        }
        if (page == sportStats!!.lastIndex) {
            next!!.visibility = View.GONE
        } else {
            next!!.visibility = View.VISIBLE
        }
    }

    private fun getSportStatistics() {
        val call = RetrofitClient.getInstance(activity!!).api.getSportStatistics()
        call.enqueue(object : Callback<List<SportStatisticsResponse>> {
            override fun onFailure(call: Call<List<SportStatisticsResponse>>, t: Throwable) {
                loadingScreen!!.visibility = View.GONE
                Toasty.error(
                    activity!!,
                    t.message!!,
                    Toasty.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<List<SportStatisticsResponse>>,
                response: Response<List<SportStatisticsResponse>>
            ) {
                if (response.body() != null) {
                    sportStats = response.body()
                    initPager()
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

    private fun initPager() {
        pagerAdapter = SportStatsPagerAdapter(activity!!.supportFragmentManager, sportStats!!)
        sportStatsPager!!.adapter = pagerAdapter
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_previous -> {
                currentPage -= 1
                onPageChanged(currentPage)
                sportStatsPager!!.setCurrentItem(currentPage, true)
            }

            R.id.iv_next -> {
                currentPage += 1
                onPageChanged(currentPage)
                sportStatsPager!!.setCurrentItem(currentPage, true)
            }
        }
    }
}
