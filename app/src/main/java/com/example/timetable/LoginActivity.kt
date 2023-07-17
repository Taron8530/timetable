package com.example.timetable

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    var TAG = "LoginActivity"
    lateinit var schoolName : EditText
    lateinit var searchSchoolName: TextView
    lateinit var schoolInfoRecyclerView : RecyclerView
    var schoolCheck = false
    var gradeCheck = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        searchSchoolName.setOnClickListener(View.OnClickListener() {
            Log.d(TAG, "onCreate:  버튼 눌 ")
            if(searchSchoolName.text.equals("")){
                Toast.makeText(this,"학교 이름을 입력 해주세요.",Toast.LENGTH_SHORT)
            }else{
                var dialog = SchoolInfoDialog(this,schoolName.text.toString())
                dialog.showDialog()
                dialog.setOnClickListener(object : SchoolInfoDialog.OnDialogClickListener{

                    override fun onClicked(schoolInfo: schoolInfoData.SchoolInfo.Row) {
                        Log.d(TAG, "onClicked: 학교이름{${schoolInfo.SCHUL_NM}} 학교 코드 : {${schoolInfo.SD_SCHUL_CODE}}")
                    }
                })

            }
        })
        Log.d(TAG, "onCreate: ")


    }
    fun initView(){
        schoolName = findViewById<EditText>(R.id.school_name)
        searchSchoolName = findViewById<TextView>(R.id.search_button)
    }
    fun schoolInfoView(){

    }
}