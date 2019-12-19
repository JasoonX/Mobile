package com.jsports.api.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jsports.R
import com.jsports.api.models.User

class UsersAdapter(
    private val context: Context,
    private val users: List<User>,
    private val seeMore:(username:String)->Unit
) :
    RecyclerView.Adapter<UsersAdapter.EventsViewHolder>() {

    class EventsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFullName:TextView = view.findViewById(R.id.tv_users_full_name)
        val tvUsername:TextView = view.findViewById(R.id.tv_users_username)
        val tvGender:TextView = view.findViewById(R.id.tv_users_gender)
        val tvCountry:TextView = view.findViewById(R.id.tv_users_country)
        val tvSeeMore:TextView = view.findViewById(R.id.tv_users_see_more)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.users_item, parent,
            false
        )
        return EventsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val user = users[position]

        holder.tvFullName.text = user.fullname
        holder.tvUsername.text = user.username
        holder.tvGender.text =
            if (user.gender == "MALE")
                context.getString(R.string.male)
            else
                context.getString(R.string.female)

        holder.tvCountry.text = user.country

        holder.tvSeeMore.setOnClickListener {
            seeMore(user.username)
        }
    }
}