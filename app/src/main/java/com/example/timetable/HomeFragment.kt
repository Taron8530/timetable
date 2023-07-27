package com.example.timetable

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date


class HomeFragment( val schoolInfo:SchoolInfo ) : Fragment() {
    lateinit var root : View
    lateinit var todayLunch : TextView
    val schoolCode = schoolInfo.schoolCode
    val line = schoolInfo.line
    val department = schoolInfo.department
    val grade = schoolInfo.grade
    val classNum = schoolInfo.classNum
    val TAG = "HomeFragment"
    val week = listOf<String>("일요일","월요일","화요일","수요일","목요일","금요일","토요일")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getLunch()
        getTimeTable()
    }
    fun initView(){
        todayLunch = root.findViewById(R.id.todayLunch)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLunch(){
//        20220302
        val week = week.get(getCurrentWeek()-1)
//        val fotmat = DateTimeFormatter.ofPattern("MM월 dd일(${week}) 오늘의 급식")
//        val showDate = LocalDateTime.now().format(fotmat)
        val todayTextView = root.findViewById<TextView>(R.id.lunch_show_date)
        val test = "${testGetShowDate()} 오늘의 급식"
        todayTextView.setText(test)
        val date = getDate()
        var apiInterface = ApiClient.getRetrofit().create(ApiInterface :: class.java)
        var call = apiInterface.getSchoolLunch(
            resources.getString(R.string.education_api_key),
            "json",
            1,
            5,
            resources.getString(R.string.area_code),
            schoolCode,
            testGetDate(),
        )
        call.enqueue(object : Callback<SchoolMealData>{
            override fun onResponse(
                call: Call<SchoolMealData>,
                response: Response<SchoolMealData>
            ) {
                if(response.isSuccessful){
                    var lunch = root.findViewById<TextView>(R.id.todayLunch)
                    var test = removeParentheses(response?.body()?.mealServiceDietInfo?.get(1)?.row?.get(0)?.DDISH_NM?.replace("<br/>","\n").toString())
                    if(test.equals("null")){
                        lunch.setText("결과가 없습니다.")
                    }else{
                        lunch.setText(test)
                    }
                    Log.d(TAG, "onResponse: ${test}")
                }
            }

            override fun onFailure(call: Call<SchoolMealData>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t}")
            }

        })
    }
    fun removeParentheses(input: String): String {
        var result = input
        val regex = "\\([^)]+\\)".toRegex() // 정규식 패턴: 괄호와 괄호 안의 내용을 찾음

        // 정규식에 해당하는 부분을 공백으로 대체하여 제거
        result = result.replace(regex, "")

        return result.trim() // 문자열 앞뒤의 공백 제거 후 반환
    }
    fun getCurrentWeek(): Int {
        val currentDate = Date()
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTime(currentDate)
        return calendar.get(Calendar.DAY_OF_WEEK)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeTable(){
        val showTextView : TextView = root.findViewById(R.id.time_table_showDate)
        val testshow = "${testGetShowDate()} 오늘의 시간표"
        showTextView.setText(testshow)
        var apiInterface = ApiClient.getRetrofit().create(ApiInterface ::class.java)
        var call = apiInterface.getGeneralTimetable(
            resources.getString(R.string.education_api_key),
            "json",
            1,
            100,
            resources.getString(R.string.area_code),
            schoolCode,
            testGetDate(),
            testGetDate(),
            grade,
            classNum
        )
//        var timetable : Array<HisTimeTableData.HisTimetable.Row>
        var timetable = listOf<String>("1교시 : 국어","2교시 : 영어","3교시 : 수학","4교시 : 사회","5교시 : 과학","6교시 : 한국사","7교시 : 창체",)
        call.enqueue(object : Callback<HisTimeTableData.HisTimetable>{
            override fun onResponse(
                call: Call<HisTimeTableData.HisTimetable>,
                response: Response<HisTimeTableData.HisTimetable>
            ) {
                if(response.isSuccessful){
//                    Log.d(TAG, "시간표: ${response.body()?.row?.get(1)?.DDDEP_NM}")
                    Log.d(TAG, "시간표: ${response}")
                }
            }

            override fun onFailure(call: Call<HisTimeTableData.HisTimetable>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t}")
            }

        })
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        val adapter = TimeTableAdapter(timetable)
        recyclerView.layoutManager = GridLayoutManager(activity?.applicationContext, 1)
        recyclerView.adapter = adapter
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate() : String{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val date = current.format(formatter)
        return date
    }
    fun testGetDate() : String{
        return "20230313"
    }
    fun testGetShowDate():String{
        return "03월 13일(월)"
    }
}


