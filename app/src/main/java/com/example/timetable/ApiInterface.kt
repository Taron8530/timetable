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
        @Query("ALL_TI_YMD") timeTableWeek:String,
        @Query("GRADE") grade:String,
        @Query("CLASS_NM") classNum:String
    ) : Call<JSONArray>
    @GET("schoolMajorinfo")
    fun getSchoolDepartmentInfo(
        @Query("KEY") key: String,
        @Query("Type") type : String,
        @Query("pIndex") pIndex : Int,
        @Query("pSize") pSize : Int,
        @Query("ATPT_OFCDC_SC_CODE") scCode: String,
        @Query("SD_SCHUL_CODE") schoolCode : String,
    )
//    @GET("schoolInfo")
//    fun getSchoolInfo(
//        @Query("KEY") key: String,
//        @Query("Type") type : String,
//        @Query("pIndex") pIndex : Int,
//        @Query("pSize") pSize : Int,
//    ) 기본 틀
}