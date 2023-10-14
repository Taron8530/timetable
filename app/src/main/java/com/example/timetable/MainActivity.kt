package com.example.timetable

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(),MealSettingClickListener {
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
    private lateinit var editProfile : Button
    private lateinit var mealSetting : androidx.appcompat.widget.SwitchCompat
    private lateinit var mealSettingSharedPreferences: SharedPreferences
    private lateinit var mealSettingEditor : SharedPreferences.Editor
    private var mealSettingChecked : Boolean = false
    private var networkChecked : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        networkCheck()
        getPermission()
        init()
        getToken()
    }
    fun init(){
        Log.d(TAG, "init: ")
        getSharedPreference()
        var schoolInfo = SchoolInfo(schoolOfficeCode,schoolCode, line,department, grade, classNum)
        homeFragment = HomeFragment(schoolInfo)
        topMenuButton = findViewById(R.id.menu_button)
        timeTableFragment = TimeTableFragment(schoolInfo)
        profileFragment = ProfileFragment(schoolInfo)
        academicCalendarFragment = AcademicCalendarFragment(schoolInfo)
        drawerLayout = findViewById(R.id.drawer_layout)
        setBottomNavigationBar()
        initHambegerMenu()
        initTopBar()
        initOnclickListener()
    }
    fun setBottomNavigationBar(){
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
        Log.d(TAG, "initHambegerMenu: ")
        SchoolName = findViewById(R.id.school_name_hamberger)
        editProfile = findViewById(R.id.profile_edit_school)
        mealSetting = findViewById(R.id.mealSetting)
        mealSettingSharedPreferences = getSharedPreferences("Userinfo", AppCompatActivity.MODE_PRIVATE)
        mealSettingChecked = mealSettingSharedPreferences.getBoolean("homeMealSetting",false)
        Log.d(TAG, "initHambegerMenu: ${mealSettingChecked}")
        mealSettingEditor = mealSettingSharedPreferences.edit()
        mealSetting.isChecked = mealSettingChecked
        setHamburgerListener()
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
    fun setHamburgerListener(){
        Log.d(TAG, "setHamburgerListener: ")
        editProfile.setOnClickListener{
            val sharedPreferences = getSharedPreferences("Userinfo",0)
            val editor = sharedPreferences?.edit()
            Log.d(TAG, "onClick: "+ editor?.clear())
            editor?.clear()
            if(editor?.commit() == true){
                val intent = Intent(applicationContext,LoginActivity :: class.java)
                startActivity(intent)
                finish()
            }
        }
        mealSetting.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                Log.d(TAG, "onCheckedChanged: ${isChecked}")
                mealSettingEditor.putBoolean("homeMealSetting",isChecked)
                mealSettingEditor.commit()
            }

        })
    }
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }
    fun getSharedPreferenceKey(key:String) : String{
        var sharedPreferences = getSharedPreferences("Userinfo",0)
        return sharedPreferences.getString(key,"").toString()
    }

    override fun mealClick() {
        TODO("Not yet implemented")
    }
    fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("testt", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("testt", token)
        })
    }
    fun getPermission(){
        if (ContextCompat.checkSelfPermission (this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            val reqPerm = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

                if (isGranted) {

                } else {

                }
            }
            reqPerm.launch(android.Manifest.permission.POST_NOTIFICATIONS)

        }
    }
    // 네트워크 체트 함수
    fun networkCheck() : Boolean {
        val networkManager = NetworkManager()
        if(!networkManager.checkNetworkState(this)) {  // 네트워크 연결 안된 상태
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}