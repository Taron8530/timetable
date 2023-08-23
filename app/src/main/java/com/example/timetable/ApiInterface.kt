package com.example.timetable

import org.json.JSONArray
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("schoolInfo")
    fun getSchoolInfo(
        @Query("KEY") key: String,
        @Query("Type") type: String,
        @Query("pIndex") pIndex: Int,
        @Query("pSize") pSize: Int,
        @Query("ATPT_OFCDC_SC_CODE") EduCode: String,
        @Query("SCHUL_NM") schoolName: String
    ): Call<schoolInfoData>
    @GET("hisTimetable")
    fun getGeneralTimetable(
        @Query("KEY") key : String,
        @Query("Type") type : String,
        @Query("pIndex") pIndex : Int,
        @Query("pSize") pSize: Int,
        @Query("ATPT_OFCDC_SC_CODE") scCode: String,
        @Query("SD_SCHUL_CODE") schoolCode:String,
        @Query("ALL_TI_YMD") startDate:String,
        @Query("GRADE") grade:String,
        @Query("CLASS_NM") classNum:String
    ) : Call<HisTimeTableData>
    @GET("hisTimetable")
    fun getSpecializedTimetable(
        @Query("KEY") key : String,
        @Query("Type") type : String,
        @Query("pIndex") pIndex : Int,
        @Query("pSize") pSize: Int,
        @Query("ATPT_OFCDC_SC_CODE") scCode: String,
        @Query("SD_SCHUL_CODE") schoolCode:String,
        @Query("ALL_TI_YMD") startDate:String,
        @Query("GRADE") grade:String,
        @Query("CLASS_NM") classNum:String,
        @Query("DDDEP_NM") departmentName : String
    ) : Call<HisTimeTableData>
    @GET("hisTimetable")
    fun getGeneralWeekTimetable(
        @Query("KEY") key : String,
        @Query("Type") type : String,
        @Query("pIndex") pIndex : Int,
        @Query("pSize") pSize: Int,
        @Query("ATPT_OFCDC_SC_CODE") scCode: String,
        @Query("SD_SCHUL_CODE") schoolCode:String,
        @Query("TI_FROM_YMD") startDate:String,
        @Query("TI_TO_YMD") endDate:String,
        @Query("GRADE") grade:String,
        @Query("CLASS_NM") classNum:String
    ) : Call<HisTimeTableData>
    @GET("hisTimetable")
    fun getSpecializedWeekTimetable(
        @Query("KEY") key : String,
        @Query("Type") type : String,
        @Query("pIndex") pIndex : Int,
        @Query("pSize") pSize: Int,
        @Query("ATPT_OFCDC_SC_CODE") scCode: String,
        @Query("SD_SCHUL_CODE") schoolCode:String,
        @Query("TI_FROM_YMD") startDate:String,
        @Query("TI_TO_YMD") endDate:String,
        @Query("GRADE") grade:String,
        @Query("CLASS_NM") classNum:String,
        @Query("DDDEP_NM") departmentName : String
    ) : Call<HisTimeTableData>
    @GET("schoolMajorinfo")
    fun getSchoolDepartmentInfo(
        @Query("KEY") key: String,
        @Query("Type") type : String,
        @Query("pIndex") pIndex : Int,
        @Query("pSize") pSize : Int,
        @Query("ATPT_OFCDC_SC_CODE") scCode: String,
        @Query("SD_SCHUL_CODE") schoolCode : String,
    ) : Call<SchoolDepartmentData>

    @GET("mealServiceDietInfo")
    fun getSchoolLunch(
        @Query("KEY") key: String,
        @Query("Type") type : String,
        @Query("pIndex") pIndex : Int,
        @Query("pSize") pSize : Int,
        @Query("ATPT_OFCDC_SC_CODE") scCode: String,
        @Query("SD_SCHUL_CODE") schoolCode : String,
        @Query("MMEAL_SC_CODE") mealScCode : String,
        @Query("MLSV_YMD") days:String
    ) : Call<SchoolMealData>

//    AcademicCalendarData
    @GET("SchoolSchedule")
    fun getSchoolAcademicCalendar(
        @Query("KEY") key: String,
        @Query("Type") type : String,
        @Query("pIndex") pIndex : Int,
        @Query("pSize") pSize : Int,
        @Query("ATPT_OFCDC_SC_CODE") officeCode:String,
        @Query("SD_SCHUL_CODE") schoolCode:String,
        @Query("AA_FROM_YMD") startDate : String,
        @Query("AA_TO_YMD") endDate:String
    ) : Call<AcademicCalendarData>

//    @GET("schoolInfo")
//    fun getSchoolInfo(
//        @Query("KEY") key: String,
//        @Query("Type") type : String,
//        @Query("pIndex") pIndex : Int,
//        @Query("pSize") pSize : Int,
//    ) : Call<>
//    기본 틀
}