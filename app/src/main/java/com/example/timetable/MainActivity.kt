package com.example.timetable

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    lateinit var homeFragment : HomeFragment
    lateinit var timeTableFragment : TimeTableFragment
    lateinit var profileFragment : ProfileFragment
    lateinit var academicCalendarFragment: AcademicCalendarFragment
    lateinit var schoolCode : String
    lateinit var department : String
    lateinit var grade : String
    lateinit var classNum : String
    lateinit var line : String
    lateinit var schoolOfficeCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }
    fun init(){
        getSharedPreference()
        var schoolInfo = SchoolInfo(schoolOfficeCode,schoolCode, line,department, grade, classNum)
        homeFragment = HomeFragment(schoolInfo)
        timeTableFragment = TimeTableFragment(schoolInfo)
        profileFragment = ProfileFragment(schoolInfo)
        academicCalendarFragment = AcademicCalendarFragment(schoolInfo)

        var bnv_main = findViewById(R.id.mainBottomNavigationView) as BottomNavigationView
        bnv_main.run { setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, homeFragment).commit()
                }
                R.id.time_table -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, timeTableFragment).commit()
                }
                R.id.academicCalendar -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, academicCalendarFragment).commit()
                    Toast.makeText(applicationContext,"준비중입니다.",Toast.LENGTH_SHORT).show()
                }
                R.id.account -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, profileFragment).commit()
                }
            }
            true
        }
            selectedItemId = R.id.home
        }
        initTopBar()
    }


    fun initTopBar(){
        var topBar = findViewById<TextView>(R.id.homeFragmentTopBar)
        var schoolNmae = getSharedPreferenceKey("schoolName")
        if(line.equals("일반고")){
            topBar.setText("${schoolNmae} ${grade} 학년 ${classNum} 반")
            return
        }
        topBar.setText("${schoolNmae} ${department} ${grade} 학년 ${classNum} 반")
    }
    fun getSharedPreference(){
        schoolCode = getSharedPreferenceKey("schoolCode")
        department = getSharedPreferenceKey("department")
        grade = getSharedPreferenceKey("grade")
        classNum = getSharedPreferenceKey("classNumber")
        line = getSharedPreferenceKey("line")
        schoolOfficeCode = getSharedPreferenceKey("schoolOfficeCode")
    }
    fun getSharedPreferenceKey(key:String) : String{
        var sharedPreferences = getSharedPreferences("Userinfo",0)
        return sharedPreferences.getString(key,"").toString()
    }
}