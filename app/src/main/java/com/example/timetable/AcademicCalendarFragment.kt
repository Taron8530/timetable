package com.example.timetable

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.security.auth.callback.Callback

@RequiresApi(Build.VERSION_CODES.O)
class AcademicCalendarFragment(val schoolInfo : SchoolInfo) : Fragment() , onCalendarClickListener{
    val TAG = "AcademicClaendarFragment"
    lateinit var root : View
    lateinit var currentDate : TextView
    lateinit var lastMonth: Button
    lateinit var nextMonth: Button
    lateinit var academicCalendar : RecyclerView // 달력
    lateinit var academicCalendarView : RecyclerView // 학사일정 출력
    lateinit var apiDateInfo : ArrayList<String>
    lateinit var scheduleList : ArrayList<String>

    lateinit var scheduleAdapter : SelectAcademicCalendarAdapter
    val WEEKDATA = listOf<String>("일","월","화","수","목","금","토")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_academic_calendar, container, false)
        // Inflate the layout for this fragment
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView() //view 설정하는 함수 호출
        initClickListener() // 클릭 리스너 설정하는 함수 호출
    }
    fun initView(){
        currentDate = root.findViewById(R.id.currentDate)
        lastMonth = root.findViewById(R.id.lastMonth)
        nextMonth = root.findViewById(R.id.nextMonth)
        academicCalendar = root.findViewById(R.id.academicCalendar)
        academicCalendarView = root.findViewById(R.id.academicCalendarView)
        CalendarUtil.currentDate = LocalDate.now()
        apiDateInfo = ArrayList()
        scheduleList = ArrayList()
    }
    fun setMonthView(){
        currentDate.setText(monthTearFromDate(CalendarUtil.currentDate))
        val daylist = dayInMonthArray(CalendarUtil.currentDate)
        val adapter = CalendarAdapter(daylist, this,apiDateInfo)
        var manager : RecyclerView.LayoutManager = GridLayoutManager(activity?.application,7)
        academicCalendar.layoutManager = manager
        academicCalendar.adapter = adapter
        Log.d(TAG, "setMonthView: ${daylist}")
        getSelectCalendar(CalendarUtil.currentDate.toString().replace("-",""))
    }
    fun monthTearFromDate(date:LocalDate) : String{
        var formatter = DateTimeFormatter.ofPattern("MM월 yyyy")
        return date.format(formatter)
    }
    fun getSelectCalendar(selectDate : String){
        Log.d(TAG, "getSelectCalendar: ${CalendarUtil.currentDate.toString().replace("-","")}")
        val apiInterface = ApiClient.getRetrofit().create(ApiInterface :: class.java)
        val call = apiInterface.getSchoolSelectAcademicCalendar(
            resources.getString(R.string.education_api_key)
        ,"json",
            1,
            100,
            schoolInfo.schoolOfficeCode,
            schoolInfo.schoolCode,
            selectDate
        )
        call.enqueue(object : retrofit2.Callback<AcademicCalendarData>{
            override fun onResponse(
                call: Call<AcademicCalendarData>,
                response: Response<AcademicCalendarData>
            ) {
                if(response.isSuccessful){
//                    val result = response.body()?.SchoolSchedule?.get(1)?.row
                    val result = response?.body()?.SchoolSchedule?.get(1)?.row
                    Log.d(TAG, "onResponse selectSchedule: ${result} ")
                    if(result != null) {
                        scheduleList.clear()
                        for (i in result) {
                            Log.d(TAG, "onResponse selectSchedule: ${result} 일정 ${i.EVENT_NM}")
                            scheduleList.add(i.EVENT_NM)
                        }
                        Log.d(TAG, "onResponse: 리스트 확인 ${scheduleList}")
                        scheduleAdapter = SelectAcademicCalendarAdapter(scheduleList)
                        academicCalendarView.layoutManager = GridLayoutManager(activity?.applicationContext, 1)
                        academicCalendarView.adapter = scheduleAdapter
                        scheduleAdapter.notifyDataSetChanged()
                    }else{
                        if(::scheduleAdapter.isInitialized){
                            scheduleList.clear()
                            scheduleAdapter.notifyDataSetChanged()
                        }

                    }

                }
            }

            override fun onFailure(call: Call<AcademicCalendarData>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
    fun getCalendar(){
        var startDate = CalendarUtil.currentDate.withDayOfMonth(1).toString().replace("-","")
        var yearMonth = YearMonth.from(CalendarUtil.currentDate)
        //첫 번째날 요일 가져오기(월:1, 일: 7)
        var endDate = startDate.slice(0..5) + yearMonth.lengthOfMonth()
        Log.d(TAG, "getCalendar: ${startDate} / ${endDate}")

        val apiInterface = ApiClient.getRetrofit().create(ApiInterface :: class.java)
        val call = apiInterface.getSchoolAcademicCalendar(
            resources.getString(R.string.education_api_key),
            "json",1,100,schoolInfo.schoolOfficeCode,schoolInfo.schoolCode,startDate,endDate
        )
        call.enqueue(object : retrofit2.Callback<AcademicCalendarData>{
            override fun onResponse(
                call: Call<AcademicCalendarData>,
                response: Response<AcademicCalendarData>
            ) {
                if(response.isSuccessful && response.body() != null){
                    val result = response.body()?.SchoolSchedule?.get(1)?.row
                    apiDateInfo.clear()
                    Log.d(TAG, "onResponse:s${response} ${result?.size}")
//                    apiDateInfo = response.body()!!.SchoolSchedule.get(1)?.row as ArrayList<AcademicCalendarData.schoolSchedule.Row>
                    if(result != null) {
                        for (i in result) {

                            var dates = if(i.AA_YMD.slice(6..7).startsWith("0")) i.AA_YMD[i.AA_YMD.length-1].toString() else i.AA_YMD.slice(6..7).toString()
                            Log.d(TAG, "onResponse: 여긴 ${dates} ${i.EVENT_NM} ${result.size}")
                            apiDateInfo.add(dates)
                        }
                        setMonthView()
                    }
                }
            }

            override fun onFailure(call: Call<AcademicCalendarData>, t: Throwable) {
                Log.e(TAG, "onFailure:${t} ")
            }

        })
    }
    private fun dayInMonthArray(date: LocalDate): ArrayList<String>{

        var dayList = ArrayList<String>()
        for(i in WEEKDATA){
            dayList.add(i)
        }
        var yearMonth = YearMonth.from(date)

        //해당 월 마지막 날짜 가져오기(예: 28, 30, 31)
        var lastDay = yearMonth.lengthOfMonth()

        //해당 월의 첫 번째 날 가져오기(예: 4월 1일)
        var firstDay = CalendarUtil.currentDate.withDayOfMonth(1)
        Log.d(TAG, "dayInMonthArray: ${firstDay.toString().replace("-","")}")
        //첫 번째날 요일 가져오기(월:1, 일: 7)
        var dayOfWeek = firstDay.dayOfWeek.value

        for(i in 1..38){
            if(i <= dayOfWeek || i > (lastDay + dayOfWeek)){
                dayList.add("")
            }else{
                dayList.add((i - dayOfWeek).toString())
            }
        }

        return dayList
    }
    fun initClickListener(){
        lastMonth.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                Log.d(TAG, "onClick: 이전달 버튼 클릭")
                CalendarUtil.currentDate = CalendarUtil.currentDate.minusMonths(1)
                getCalendar()
            }
        })
        getCalendar()
        nextMonth.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                CalendarUtil.currentDate = CalendarUtil.currentDate.plusMonths(1)
                Log.d(TAG, "onClick: 다음달 버튼 클릭 ${monthTearFromDate(CalendarUtil.currentDate)}")
                getCalendar()
            }

        })
    }

    override fun onItemClick(dayText: String) {
        val monthValue = CalendarUtil.currentDate.monthValue.toString()
        var days = if(dayText.length == 2) dayText else "0${dayText}"
        var month = if(monthValue.length == 2) monthValue else "0${monthValue}"
        var year = CalendarUtil.currentDate.year.toString()
        val requestData = year+month + days
        Log.d(TAG, "onItemClick: ${requestData}")
        getSelectCalendar(requestData)

    }
}