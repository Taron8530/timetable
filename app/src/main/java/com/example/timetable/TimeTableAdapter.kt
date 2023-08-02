package com.example.timetable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//private val timeTable: Array<HisTimeTableData.HisTimetable.Row>
class TimeTableAdapter(private val timeTable: ArrayList<TimeTableData>) :
    RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_time_table, parent, false)
        return TimeTableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {
        val item = timeTable[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return timeTable.size
    }

    class TimeTableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val className: TextView = itemView.findViewById(R.id.className)
        private val period : TextView = itemView.findViewById(R.id.period)

        fun bind(item: TimeTableData) {
            className.text = item.className
            period.text = "${item.period} 교시"
        }
    }
}