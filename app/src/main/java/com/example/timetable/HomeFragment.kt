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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

// 조식 코드 : 1, 중식 코드 : 2, 석식 코드 : 3
@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment( val schoolInfo:SchoolInfo ) : Fragment() {
    lateinit var root : View
    lateinit var todayLunch : TextView
    lateinit var todayLunchBanner : TextView
    val schoolCode = schoolInfo.schoolCode
    val line = schoolInfo.line
    val department = schoolInfo.department
    val schoolOfficeCode = schoolInfo.schoolOfficeCode
    val grade = schoolInfo.grade
    val classNum = schoolInfo.classNum
    val TAG = "HomeFragment"
    val week = listOf<String>("일","월","화","수","목","금","토")


    val timetables = mapOf(
        1 to LocalTime.of(9, 0),  // 1교시 시작 시간
        2 to LocalTime.of(10, 0),  // 2교시 시작 시간
        3 to LocalTime.of(11, 0), // 3교시 시작 시간
        4 to LocalTime.of(12,0),
        5 to LocalTime.of(14,0),
        6 to LocalTime.of(15,0),
        7 to LocalTime.of(16,0)
    )
    val mealTimes = mapOf(
        "1" to LocalTime.of(8, 0),  // 1교시 시작 시간
        "2" to LocalTime.of(14, 0),
        "3" to LocalTime.of(19, 0)// 2교시 시작 시간
    )
    val mealNames = listOf<String>("조식","중식","석식")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getLunch()
        getTimeTable()
    }

    fun initView(){
        todayLunch = root.findViewById(R.id.todayLunch)
        val showDate : TextView = root.findViewById(R.id.homeTodayDate)
        todayLunchBanner = root.findViewById(R.id.lunch_show_date)
//        showDate.setText(getShowDate())
        showDate.setText(getShowDate())
        Log.d(TAG, "initView: ${getDate()} ${getShowDate()}")
    }
    fun getLunch(){
//        20220302
        val week = week.get(getCurrentWeek()-1)
//        val fotmat = DateTimeFormatter.ofPattern("MM월 dd일(${week}) 오늘의 급식")
//        val showDate = LocalDateTime.now().format(fotmat)
        val date = getDate()
        var apiInterface = ApiClient.getRetrofit().create(ApiInterface :: class.java)
        val mealScCode = getCurrentMealTime(LocalTime.of(17,1),mealTimes)
        var call = apiInterface.getSchoolLunch(
            resources.getString(R.string.education_api_key),
            "json",
            1,
            5,
            schoolOfficeCode,
            schoolCode,
            mealScCode,
            date,
        )
        if(getMealSetting()){
            call =apiInterface.getSchoolLunch(
                resources.getString(R.string.education_api_key),
                "json",
                1,
                5,
                schoolOfficeCode,
                schoolCode,
                "2",
                date)
        }else{
            todayLunchBanner.setText("오늘의 ${mealNames.get(mealScCode.toInt()-1)}")
        }

        call.enqueue(object : Callback<SchoolMealData>{
            override fun onResponse(
                call: Call<SchoolMealData>,
                response: Response<SchoolMealData>
            ) {
                if(response.isSuccessful){
                    var test = ""
                    var lunch = root.findViewById<TextView>(R.id.todayLunch)
                    Log.d(TAG, "onResponse: 급식 테스트${response?.body()?.mealServiceDietInfo?.get(0)?.head?.get(0)?.list_total_count}")
                    Log.d(TAG, "onResponse: 급식 통신 $response")
                    test = removeParentheses(response?.body()?.mealServiceDietInfo?.get(1)?.row?.get(0)?.DDISH_NM?.replace("<br/>","\n").toString())
                    if(response?.body()?.mealServiceDietInfo?.get(0)?.head?.get(0)?.list_total_count ==3){
                        test = removeParentheses(response?.body()?.mealServiceDietInfo?.get(1)?.row?.get(1)?.DDISH_NM?.replace("<br/>","\n").toString())
                    }
                    if(test.equals("null")){
                        lunch.setText("교육정보개방포털에서 비공개된 자료로 \n 급식정보가 없습니다.")
                    }else{
                        lunch.setText(test)
                    }
                    Log.d(TAG, "onResponse: ${response?.body()?.mealServiceDietInfo?.get(1)?.row?.get(0)?.MMEAL_SC_CODE}")
                }
            }

            override fun onFailure(call: Call<SchoolMealData>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t}")
            }

        })
    }
    fun removeParentheses(input: String): String {
        var result = input
        val regex = "\\([^*)]+\\)".toRegex() // 정규식 패턴: 괄호와 괄호 안의 내용을 찾음

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
    fun getTimeTable(){
        var apiInterface = ApiClient.getRetrofit().create(ApiInterface ::class.java)
        var call = apiInterface.getSpecializedTimetable(
            resources.getString(R.string.education_api_key),
            "json",
            1,
            100,
            schoolOfficeCode,
            schoolCode,
            getDate(),
            grade,
            classNum,
            department
        )
        if(department.equals("")){
            call = apiInterface.getGeneralTimetable(
                resources.getString(R.string.education_api_key),
                "json",
                1,
                100,
                schoolOfficeCode,
                schoolCode,
                getDate(),
                grade,
                classNum
            )
        }
//        var timetable : Array<HisTimeTableData.HisTimetable.Row>
        var timetable = ArrayList<TimeTableData>()
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        Log.d(TAG, "getTimeTable: ${timetable}")
        val adapter = TimeTableAdapter(timetable)
        recyclerView.layoutManager = GridLayoutManager(activity?.applicationContext, 1)
        recyclerView.adapter = adapter
        call.enqueue(object : Callback<HisTimeTableData>{
            override fun onResponse(
                call: Call<HisTimeTableData>,
                response: Response<HisTimeTableData>
            ) {
                if(response.isSuccessful){
                    Log.d(TAG, "시간표: ${response.body()?.hisTimetable?.get(1)?.row}")
                    Log.d(TAG, "시간표: ${response}")
                    var array = response.body()?.hisTimetable?.get(1)?.row
                    if(array != null) {
                        for (value in array) {
                            Log.d(
                                TAG,
                                "onResponse for loop: ${value.PERIO} 교시 : ${value.ITRT_CNTNT}"
                            )
                            timetable.add(
                                TimeTableData(
                                    value.PERIO,
                                    value.ITRT_CNTNT.replace("*", "")
                                )
                            )
                            Log.d(TAG, "onResponse for loop 1: ${timetable}")
                        }
                        val currentTime = getCurrentPeriod(LocalTime.of(15, 10),timetables)
                        Log.d(TAG, "onResponse: ${currentTime}")
                        if(currentTime.toInt() <= array.size){
                            recyclerView.scrollToPosition(currentTime.toInt() - 1)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            override fun onFailure(call: Call<HisTimeTableData>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t}")
            }

        })
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate() : String{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val date = current.format(formatter)
        return date
    }
    fun getShowDate() : String{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("M월d일(${week.get(getCurrentWeek()-1)})")
        val date = current.format(formatter)
        return date
    }
    fun testGetDate() : String{
        return "20230313"
    }
    fun testGetShowDate():String{
        return "03월 13일(월)"
    }
    fun getCurrentPeriod(currentTime: LocalTime, timetable: Map<Int, LocalTime>): String {
        for ((period, startTime) in timetable) {
            if (currentTime.isBefore(startTime)) {
                return period.toString()
            }
        }

        return "0" // 아직 수업이 시작하지 않았음을 나타내는 값
    }
    fun getCurrentMealTime(currentTime: LocalTime, mealTimes: Map<String, LocalTime>): String {
        for ((period, startTime) in mealTimes) {
            if (currentTime.isBefore(startTime)) {
                Log.d(TAG, "getCurrentMealTime: ${currentTime} ${period}")
                return period.toString()
            }
        }

        return "2" // 아직 수업이 시작하지 않았음을 나타내는 값
    }
    fun getMealSetting() : Boolean{
        val sharedPreferences = activity?.getSharedPreferences("Userinfo",0)
        return sharedPreferences?.getBoolean("homeMealSetting",false)!!
    }
}


