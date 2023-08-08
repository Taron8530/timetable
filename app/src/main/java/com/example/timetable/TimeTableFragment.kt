package com.example.timetable

import android.app.ActionBar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.setPadding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TimeTableFragment(val schoolInfo: SchoolInfo) : Fragment() {
    lateinit var root : View
    val schoolCode = schoolInfo.schoolCode
    val line = schoolInfo.line
    val department = schoolInfo.department
    val schoolOfficeCode = schoolInfo.schoolOfficeCode
    val grade = schoolInfo.grade
    val classNum = schoolInfo.classNum
    val TAG = "TimeTableFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_time_table, container, false)
        // Inflate the layout for this fragment
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTimeTable()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeTable(){
        val (start, end) = testGetWeek()

        Log.d(TAG, "getTimeTable: ${start} , end ${end}")
        var api : ApiInterface = ApiClient.getRetrofit().create(ApiInterface::class.java)
        var call = api.getSpecializedWeekTimetable(
            resources.getString(R.string.education_api_key),
            "json",
            1,
            100,
            schoolOfficeCode,
            schoolCode,
            start,
            end,
            grade,
            classNum,
            department
        )
        if(department.equals("")){
            call = api.getGeneralWeekTimetable(
                resources.getString(R.string.education_api_key),
                "json",
                1,
                100,
                schoolOfficeCode,
                schoolCode,
                start,
                end,
                grade,
                classNum
            )
        }
        call.enqueue(object : Callback<HisTimeTableData>{
            override fun onResponse(
                call: Call<HisTimeTableData>,
                response: Response<HisTimeTableData>
            ) {
                if(response.isSuccessful){
                    Log.d(TAG, "onResponse: 시간표 ${response}")
                    val timetableData = response.body()
                    if (timetableData != null) {
                        val parsedData = parseTimetableData(timetableData)
                        for ((period, subjects) in parsedData) {
                                Log.d(TAG, "onResponse: Period: $period")
                                for (subject in subjects) {
                                    Log.d(TAG, "onResponse:  Subject: $subject")
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<HisTimeTableData>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t}")
            }

        })
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStartAndEndOfThisWeek(): Pair<String, String> {
        val today = LocalDate.now()

        // 오늘이 일주일의 몇번째 인지 월요일=1, 화요일=2, 수요일=3.....
        val day = today.dayOfWeek.value
        var adjustedDay = day
        if (adjustedDay == 7) {
            adjustedDay = 0
        }

        // "오늘 날짜"에서 "오늘이 일주일의 몇번째"를 빼면 요번주의 시작일이 나온다.
        // 예를 들어 "2022.9.8 목요일" 이라면 "목요일=4" [9.8 - 4일 = 9월4일 일요일]
        val start = today.minusDays(adjustedDay.toLong()).plusDays(1)
        val end = start.plusDays(4)

        return Pair(getFormattedDate(start), getFormattedDate(end))
    }
    fun testGetWeek() : Pair<String,String>{
        return Pair("202303013","20230317")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return date.format(formatter)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun parseTimetableData(timetableData: HisTimeTableData): Map<String, List<String>> {
        val resultMap = mutableMapOf<String, MutableList<String>>()

        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        for (row in timetableData.hisTimetable[1].row) {
            val dateStr = row.ALL_TI_YMD
            val date = LocalDate.parse(dateStr, dateFormatter)
            val period = row.PERIO
            val subject = row.ITRT_CNTNT

            if (!resultMap.containsKey(period)) {
                resultMap[period] = mutableListOf()
            }
            resultMap[period]?.add(subject)
        }

        return resultMap
    }

}