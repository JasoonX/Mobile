package com.jsports.api.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jsports.R
import com.jsports.api.RetrofitClient
import com.jsports.api.models.responses.EventResponse
import com.jsports.api.models.responses.MessageResponse
import com.jsports.helpers.RetrofitCallback

class EventsAdapter(private val context: Context,
                    private val events: List<EventResponse>,
                    private val deleteEventPressed:(id:Long)->Unit,
                    private val isCurrentUserEvents:Boolean = true) :
    RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    class EventsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
        val tvDistance: TextView = view.findViewById(R.id.tv_distance)
        val tvResultTime: TextView = view.findViewById(R.id.tv_result_time)
        val tvComment: TextView = view.findViewById(R.id.tv_comment)
        val ivDeleteEvent: ImageView = view.findViewById(R.id.iv_delete_event)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.events_item, parent,
            false
        )
        return EventsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val event = events[position]

        val date: String = event.dateTime.substring(0, 10)
        val time: String = event.dateTime.substring(11, 16)
        val distance: Float = event.result.distance
        val distanceString = if (distance >= 1000) {
            if (distance % 1000 == 0.0F) {
                "${(distance / 1000).toInt()}${context.getString(R.string.km)}"
            } else {
                "${distance / 1000}${context.getString(R.string.km)}"
            }
        } else {
            "$distance${context.getString(R.string.m)}"
        }

        val resultTime: Float = event.result.time

        val resultTimeString = if (resultTime >= 60) {
            val minutes = (resultTime / 60).toInt()
            if (resultTime % 60 == 0.0F) {
                "$minutes${context.getString(R.string.min)}"
            } else {
                val seconds = (resultTime - (minutes * 60)).toInt()
                "$minutes${context.getString(R.string.min)} $seconds${context.getString(R.string.s)}"
            }
        } else {
            "${resultTime}${context.getString(R.string.s)}"
        }

        val comment =
            if (event.comment == null || event.comment.isEmpty())
                context.getString(R.string.no_comment)
            else
                event.comment

        holder.tvDate.text = date
        holder.tvTime.text = time
        holder.tvDistance.text = distanceString
        holder.tvResultTime.text = resultTimeString
        holder.tvComment.text = comment

        holder.ivDeleteEvent.setOnClickListener { deleteEventPressed(event.id) }
        if(!isCurrentUserEvents){
            holder.ivDeleteEvent.visibility = View.GONE
        }
    }
}