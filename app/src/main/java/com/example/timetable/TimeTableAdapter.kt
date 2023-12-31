package com.example.timetable

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalTime

//private val timeTable: Array<HisTimeTableData.HisTimetable.Row>
@RequiresApi(Build.VERSION_CODES.O)
class TimeTableAdapter(private val timeTable: ArrayList<TimeTableData>) :

    RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder>() {
    val TAG = "TimeTableAdapter"
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
    @RequiresApi(Build.VERSION_CODES.O)
    class TimeTableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val TAG = "TimeTableViewHolder"
        @RequiresApi(Build.VERSION_CODES.O)
        val timetables = mapOf(
            1 to LocalTime.of(9, 0),  // 1교시 시작 시간
            2 to LocalTime.of(10, 0),  // 2교시 시작 시간
            3 to LocalTime.of(11, 0), // 3교시 시작 시간
            4 to LocalTime.of(12,0),
            5 to LocalTime.of(14,0),
            6 to LocalTime.of(15,0),
            7 to LocalTime.of(16,0)
        )
        val currentTime = LocalTime.now()
        private val className: TextView = itemView.findViewById(R.id.className)
        private val layout : LinearLayout = itemView.findViewById(R.id.periodLinearLayout)
        private val period : TextView = itemView.findViewById(R.id.period)
        val currentPeriod = getCurrentPeriod(currentTime,timetables)
        val nextPeriod = currentPeriod.toInt().plus(1).toString()

        fun bind(item: TimeTableData) {
            className.text = item.className
            period.text = "${item.period} 교시"
            Log.d(TAG, "bind: ${currentPeriod} ${item.period} gd ${currentTime} nex ${nextPeriod}")
            if(currentPeriod.equals(item.period)){
                layout.setBackgroundResource(R.drawable.current_timetable)
                Log.d(TAG, "bind: gd ${currentPeriod} ${item.period}")
            }
//            if(nextPeriod.equals(item.period)){
//                layout.setBackgroundResource(R.drawable.next_period_timetable)
//                Log.d(TAG, "bind: gd ${currentPeriod} ${item.period}")
//            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun getCurrentPeriod(currentTime: LocalTime, timetable: Map<Int, LocalTime>): String {
            for ((period, startTime) in timetable) {
                val endTime = startTime.plusMinutes(60) // 50분 간격

                if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                    return period.toString()
                }
            }

            return "0"// 아직 수업이 시작하지 않았음을 나타내는 값
        }
    }
}