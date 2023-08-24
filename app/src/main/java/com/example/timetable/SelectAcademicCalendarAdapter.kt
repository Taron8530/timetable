package com.example.timetable

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectAcademicCalendarAdapter(val scheduleList : ArrayList<String>) :
    RecyclerView.Adapter<SelectAcademicCalendarAdapter.SelectAcademicCalendarViewHolder>() {
    class SelectAcademicCalendarViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val schedule = itemView.findViewById<TextView>(R.id.schedule)
        fun bind( item: String) {
            schedule.text = item
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectAcademicCalendarViewHolder {
        val view =
        LayoutInflater.from(parent.context).inflate(R.layout.schedule_item, parent, false)

        return SelectAcademicCalendarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    override fun onBindViewHolder(holder: SelectAcademicCalendarViewHolder, position: Int) {
        val item = scheduleList[position]
        holder.bind(item)
    }
}