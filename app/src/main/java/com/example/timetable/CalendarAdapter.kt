package com.example.timetable

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

@RequiresApi(Build.VERSION_CODES.O)
class CalendarAdapter(private val dayList: ArrayList<String>,
                      private val onItemListener: onCalendarClickListener,
                        private val apiDateInfo : ArrayList<AcademicCalendarData.schoolSchedule.Row>):
    RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {
    val TAG ="CalendarAdapter"
    var selectPosition = -1


    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val dayText: TextView = itemView.findViewById(R.id.dayText)
        val scheduleCheck : TextView = itemView.findViewById(R.id.scheduleCheck)
    }

    //화면 설정z
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_item, parent, false)

        return ItemViewHolder(view)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        //날짜 변수에 담기
        var day = dayList[holder.adapterPosition]

//        holder.dayText.text = day]
        Log.d(TAG, "onBindViewHolder: ${day.equals(CalendarUtil.currentDate)} ${day} / ${CalendarUtil.currentDate} / ${apiDateInfo.get(1).AA_YMD}")
        if(day.equals("")){
            holder.dayText.text = ""
        }else{
            //해당 일자를 넣는다.
            holder.dayText.text = day

            //현재 날짜 색상 칠하기
            if(day.equals(CalendarUtil.currentDate.dayOfMonth.toString()) && selectPosition == -1){
                Log.d(TAG, "onBindViewHolder: 호출 ")
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            }
            if(holder.adapterPosition == selectPosition ){
                Log.d(TAG, "onBindViewHolder: ${selectPosition}")
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            }
        }
        //텍스트 색상 지정(토,일)
        if((position +1) % 7 == 0){ //토요일은 파랑
            holder.dayText.setTextColor(Color.BLUE)

        }else if( position == 0 || position % 7 == 0){ //일요일은 빨강
            holder.dayText.setTextColor(Color.RED)
            Log.d(TAG, "onBindViewHolder: position ${position}")
        }

        //날짜 클릭 이벤트
        holder.itemView.setOnClickListener {
            //인터페이스를 통해 날짜를 넘겨준다.
            onItemListener.onItemClick(day)
            if(holder.adapterPosition !in 0..6){
                selectPosition = holder.adapterPosition
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }
}