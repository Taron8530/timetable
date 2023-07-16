package com.example.timetable

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class SchoolInfoAdapter (private val context: Context, private val datas: List<schoolInfoData.SchoolInfo.Row>) : RecyclerView.Adapter<SchoolInfoAdapter.ViewHolder>() {
    var TAG = "SchoolInfoAdapter"
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val schoolName = view.findViewById<TextView>(R.id.school_name_item)
        private val schoolLocation = view.findViewById<TextView>(R.id.school_location_item)

        fun bind(items : schoolInfoData.SchoolInfo.Row){
            schoolName.text = items.SCHUL_NM
            schoolLocation.text = items.ORG_RDNMA
            Log.d(TAG, "bind: 호출됨 " + items.SCHUL_NM)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val View = LayoutInflater.from(context).inflate(R.layout.school_info_item,parent,false)
        Log.d(TAG, "onCreateViewHolder: 호출됨")
        return ViewHolder(View)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ${datas.size}")
        return datas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: 호출됨 position[$position]")
        holder.bind(datas[position])
    }
}
