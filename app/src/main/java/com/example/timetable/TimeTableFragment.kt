package com.example.timetable

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class TimeTableFragment(val schoolInfo: SchoolInfo) : Fragment() {
    lateinit var root : View
    lateinit var tableLayout: TableLayout
    val schoolCode = schoolInfo.schoolCode
    val line = schoolInfo.line
    val department = schoolInfo.department
    val schoolOfficeCode = schoolInfo.schoolOfficeCode
    val grade = schoolInfo.grade
    val classNum = schoolInfo.classNum
    val TAG = "TimeTableFragment"
    val layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f)
    lateinit var weekDate: TextView

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
        init()
        getTimeTable()
    }
    fun init(){
        tableLayout = root.findViewById(R.id.tablelayout)
        weekDate = root.findViewById(R.id.time_table_week)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeTable(){
        val (start, end) = getStartAndEndOfThisWeek()
        weekDate.setText("$start ~ $end")

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
                    Log.d(TAG, "onResponse: ${timetableData} ")
                    if (timetableData?.hisTimetable == null) {
                        tableLayout.visibility = View.GONE

                    }else{
                        val parsedData = parseTimetableData(timetableData)
                        val margin = resources.getDimensionPixelSize(R.dimen.cell_margin)
                        for ((period, subjects) in parsedData) {
                            val row = TableRow(context)

                            row.layoutParams = TableLayout.LayoutParams(
                                TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT
                            )
                            Log.d(TAG, "onResponse: Period: $period 교시")
                            val periodTextView = createTextView()
                            periodTextView.text = "${period}교시"
                            row.addView(periodTextView)
                            for (subject in subjects) {
                                val subjectTextView = createTextView()
                                subjectTextView.setTextSize(12F)
                                subjectTextView.text = subject
                                row.addView(subjectTextView)
                                Log.d(TAG, "onResponse:  Subject: $subject")
                            }
                            tableLayout.addView(row)
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

        val day = today.dayOfWeek.value
        var adjustedDay = day
        if (adjustedDay == 7) {
            adjustedDay = 0
        }

        val start = today.minusDays(adjustedDay.toLong()).plusDays(1)
        val end = start.plusDays(4)

        return Pair(getFormattedDate(start), getFormattedDate(end))
    }
    fun testGetWeek() : Pair<String,String>{
        return Pair("20230821","20230825")
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
        val weekdays = listOf("월", "화", "수", "목", "금")
        val periods = listOf("1", "2", "3", "4", "5", "6", "7")

        for (row in timetableData.hisTimetable[1].row) {
            val dateStr = row.ALL_TI_YMD
            val date = LocalDate.parse(dateStr, dateFormatter)
            val weekday = date.dayOfWeek.value % 7 -1 // 월(1)~일(7)을 월(0)~일(6)로 변환
            val period = row.PERIO
            val subject = row.ITRT_CNTNT

            if (!resultMap.containsKey(period)) {
                resultMap[period] = MutableList(weekdays.size) { "" }
            }

            val subjectList = resultMap[period]!!
            subjectList[weekday] = subject
        }

        // Remove empty periods
        resultMap.keys.toList().forEach { period ->
            if (resultMap[period]!!.all { it.isBlank() }) {
                resultMap.remove(period)
            }
        }

        return resultMap
    }
    fun createTextView() : TextView{
        var column :TextView = TextView(context)
        val margin = resources.getDimensionPixelSize(R.dimen.cell_margin)
        column.layoutParams = layoutParams
        column.gravity = Gravity.CENTER
        column.textAlignment = View.TEXT_ALIGNMENT_INHERIT
        column.setBackgroundResource(R.drawable.cell_background)
        column.setPadding(margin, margin, margin, margin)
        return column
    }
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