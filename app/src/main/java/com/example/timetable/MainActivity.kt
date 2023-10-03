package com.example.timetable

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    val TAG ="MainActivity"
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
    lateinit var topMenuButton : Button
    lateinit var drawerLayout: DrawerLayout
    lateinit var SchoolName : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }
    fun init(){
        getSharedPreference()
        var schoolInfo = SchoolInfo(schoolOfficeCode,schoolCode, line,department, grade, classNum)
        homeFragment = HomeFragment(schoolInfo)
        topMenuButton = findViewById(R.id.menu_button)
        timeTableFragment = TimeTableFragment(schoolInfo)
        profileFragment = ProfileFragment(schoolInfo)
        academicCalendarFragment = AcademicCalendarFragment(schoolInfo)
        drawerLayout = findViewById(R.id.drawer_layout)

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
                }
            }
            true
        }
            selectedItemId = R.id.home
        }
        initHambegerMenu()
        initTopBar()
        initOnclickListener()
    }

    fun initOnclickListener(){
        topMenuButton.setOnClickListener{
            Log.d(TAG, "initOnclickListener: dfdfd")
            if(drawerLayout.isDrawerOpen(Gravity.RIGHT)){
                Log.d(TAG, "initOnclickListener: open")
                drawerLayout.closeDrawer(Gravity.RIGHT)
            }else{
                Log.d(TAG, "initOnclickListener: close")
                drawerLayout.openDrawer(Gravity.RIGHT)
            }
        }
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
    fun initHambegerMenu(){
        SchoolName = findViewById(R.id.school_name_hamberger)
        var classInfo = findViewById<TextView>(R.id.classInfoHamberger)
        var schoolName = getSharedPreferenceKey("schoolName")
        if(line.equals("일반고")){
            SchoolName.setText(schoolName)
            classInfo.setText("${grade} 학년 ${classNum} 반")
            return
        }
        classInfo.setText("${department}\n${grade} 학년 ${classNum} 반")
        SchoolName.setText(schoolName)
    }
    fun getSharedPreferenceKey(key:String) : String{
        var sharedPreferences = getSharedPreferences("Userinfo",0)
        return sharedPreferences.getString(key,"").toString()
    }
}