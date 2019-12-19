package com.jsports.fragments.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jsports.helpers.LocaleHelper
import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.adapters.EventsAdapter
import com.jsports.api.models.Page
import com.jsports.api.models.User
import com.jsports.api.models.requests.EventRequest
import com.jsports.api.models.responses.EventResponse
import com.jsports.api.models.responses.MessageResponse
import com.jsports.dialogs.AddEventDialog
import com.jsports.dialogs.SimpleDialog
import com.jsports.helpers.getErrorMessageFromJSON
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EventsFragment : Fragment(), View.OnClickListener {

    private lateinit var eventsPage: Page<EventResponse>
    private lateinit var loadingScreen: FrameLayout
    private var currentPage = 0
    private lateinit var currentSportsDiscipline: String
    private lateinit var sportsDisciplines: List<String>
    private lateinit var rvEvents: RecyclerView
    private lateinit var ivNextEventsPage: ImageView
    private lateinit var ivPreviousEventsPage: ImageView
    private lateinit var tvCurrentPage: TextView
    private lateinit var llNoEvents: LinearLayout
    private lateinit var tvNoEvents: TextView
    private lateinit var tvAddEvent: TextView
    private lateinit var spinnerDisciplines: Spinner
    private lateinit var ivAddEvent: ImageView

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
        tvCurrentPage.text = (currentPage + 1).toString()

        llNoEvents = view.findViewById(R.id.ll_no_events)
        llNoEvents.visibility = View.GONE

        tvNoEvents = view.findViewById(R.id.tv_no_events)

        tvAddEvent = view.findViewById(R.id.tv_add_event)
        tvAddEvent.setOnClickListener(this)

        ivAddEvent = view.findViewById(R.id.iv_add_event)
        ivAddEvent.setOnClickListener(this)

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
                    if (sportsDisciplines.isNotEmpty()) {
                        currentSportsDiscipline = sportsDisciplines[0]
                        initSpinner()
                        getEventsPage(currentPage, currentSportsDiscipline)
                    } else {
                        tvNoEvents.text = getString(R.string.no_sport_disciplines)
                        llNoEvents.visibility = View.VISIBLE
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
                    if (eventsPage.first) {
                        ivPreviousEventsPage.visibility = View.INVISIBLE
                    } else {
                        ivPreviousEventsPage.visibility = View.VISIBLE
                    }
                    if (eventsPage.last) {
                        ivNextEventsPage.visibility = View.INVISIBLE
                    } else {
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
        val eventsAdapter = EventsAdapter(activity!!, eventsPage.content, ::deleteEventPressed)
        if (eventsPage.numberOfElements == 0) {
            tvNoEvents.text = getString(R.string.no_sport_disciplines)
            llNoEvents.visibility = View.VISIBLE

        } else {
            llNoEvents.visibility = View.GONE

        }
        rvEvents.adapter = eventsAdapter
    }

    private fun deleteEventPressed(id: Long) {
        val dialog = SimpleDialog(activity!!, getString(R.string.delete_event_message), {
            deleteEvent(id)
        })
        dialog.show(activity!!.supportFragmentManager, null)
    }

    private fun deleteEvent(id: Long) {
        loadingScreen.visibility = View.VISIBLE
        val call = RetrofitClient.getInstance(activity!!).api.deleteEvent(id)

        call.enqueue(object : Callback<MessageResponse> {
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                loadingScreen.visibility = View.GONE
                Toasty.error(
                    activity!!,
                    t.message!!,
                    Toasty.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.body() != null) {
                    Toasty.success(activity!!, response.body()!!.message, Toasty.LENGTH_LONG).show()
                    if (currentPage != 0 && eventsPage.numberOfElements == 1) {
                        currentPage -= 1
                    }
                    getEventsPage(currentPage, currentSportsDiscipline)
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

    private fun initSpinner() {
        val localizedSportDisciplines =
            sportsDisciplines.map { discipline -> getString(LocaleHelper.disciplineStringResources[discipline]!!) }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter(
                activity!!,
                R.layout.spinner_item_disciplines,
                localizedSportDisciplines
            )
        adapter.setDropDownViewResource(R.layout.spinner_item_disciplines)

        spinnerDisciplines = view!!.findViewById(R.id.spin_sport_disciplines)


        spinnerDisciplines.adapter = adapter

        spinnerDisciplines.setSelection(0)

        spinnerDisciplines.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View,
                position: Int, id: Long
            ) {
                if (currentSportsDiscipline != sportsDisciplines[position]) {
                    currentSportsDiscipline = sportsDisciplines[position]
                    getEventsPage(0, currentSportsDiscipline)
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
    }

    private fun addEventPressed() {
        val dialog = AddEventDialog(currentSportsDiscipline, ::addEvent)
        dialog.show(activity!!.supportFragmentManager, null)
    }

    private fun addEvent(request: EventRequest) {
        loadingScreen.visibility = View.VISIBLE
        val call = RetrofitClient.getInstance(activity!!).api.addEvent(request)

        call.enqueue(object : Callback<MessageResponse> {
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                loadingScreen.visibility = View.GONE
                Toasty.error(
                    activity!!,
                    t.message!!,
                    Toasty.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.body() != null) {
                    Toasty.success(activity!!, response.body()!!.message, Toasty.LENGTH_LONG).show()
                    getEventsPage(currentPage, currentSportsDiscipline)
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_next_events -> {
                if (!eventsPage.last) {
                    currentPage += 1
                    tvCurrentPage.text = (currentPage + 1).toString()
                    getEventsPage(currentPage, currentSportsDiscipline)
                }
            }

            R.id.iv_previous_events -> {
                if (!eventsPage.first) {
                    currentPage -= 1
                    tvCurrentPage.text = (currentPage + 1).toString()
                    getEventsPage(currentPage, currentSportsDiscipline)
                }
            }

            R.id.tv_add_event -> addEventPressed()

            R.id.iv_add_event -> addEventPressed()
        }
    }
}
