package com.example.timetable

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {
    var TAG = "LoginActivity"
    lateinit var schoolName : EditText
    lateinit var searchSchoolName: TextView
    lateinit var schoolGradeAdapter : ArrayAdapter<String>
    lateinit var schoolClassAdapter : ArrayAdapter<String>
    lateinit var schoolGradeSpinner : Spinner
    lateinit var schoolClassSpinner:Spinner
    lateinit var schoolGrade : Array<String>
    lateinit var schoolClassNumber : Array<String>
    var schoolCheck = false
    var gradeCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        searchSchoolName.setOnClickListener(View.OnClickListener() {
            Log.d(TAG, "onCreate:  버튼 눌 ")
            if(schoolName.text.equals("")){
                Toast.makeText(this,"학교 이름을 입력 해주세요.",Toast.LENGTH_SHORT)
            }else{
                var dialog = SchoolInfoDialog(this,schoolName.text.toString())
                dialog.showDialog()
                dialog.setOnClickListener(object : SchoolInfoDialog.OnDialogClickListener{

                    override fun onClicked(schoolInfo: schoolInfoData.SchoolInfo.Row) {
                        schoolCheck = true
                        searchSchoolName.isClickable = false
                        Log.d(TAG, "onClicked: 학교이름{${schoolInfo.SCHUL_NM}} 학교 코드 : {${schoolInfo.SD_SCHUL_CODE}}")
                    }
                })

            }
        })
        schoolName.setOnClickListener(View.OnClickListener {
            schoolName.text.clear()
            schoolCheck = false
            searchSchoolName.isClickable = true
        })
        Log.d(TAG, "onCreate: ")


    }
    fun initView(){
        schoolName = findViewById<EditText>(R.id.school_name)
        searchSchoolName = findViewById<TextView>(R.id.search_button)
        schoolGradeSpinner = findViewById(R.id.school_Grade)
        schoolClassSpinner = findViewById(R.id.school_Class_Number)
        schoolGrade = resources.getStringArray(R.array.gradeList)
        schoolClassNumber = resources.getStringArray(R.array.classNum)
        schoolGradeAdapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,schoolGrade)
        schoolClassAdapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,schoolClassNumber)
        schoolGradeSpinner.adapter = schoolGradeAdapter
        schoolClassSpinner.adapter = schoolClassAdapter
    }

}