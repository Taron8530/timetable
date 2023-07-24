package com.example.timetable

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import kotlinx.coroutines.selects.select
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class LoginActivity : AppCompatActivity() {
    var TAG = "LoginActivity"
    lateinit var schoolName: EditText
    lateinit var searchSchoolName: TextView
    lateinit var schoolGradeAdapter: ArrayAdapter<String>
    lateinit var schoolClassAdapter: ArrayAdapter<String>

    lateinit var schoolGradeSpinner: Spinner
    lateinit var schoolClassSpinner: Spinner
    lateinit var schoolDepartmentSpinner : Spinner

    lateinit var schoolGrade: Array<String>
    lateinit var schoolClassNumber: Array<String>

    lateinit var selectSchoolInfo: schoolInfoData.SchoolInfo.Row
    lateinit var schoolDepartment: ArrayList<String>
    lateinit var signInButton: Button

    lateinit var classNumber: String
    lateinit var gradeNumber: String
    var department: String = ""
    var schoolCheck = false
    var gradeCheck = false
    var classCheck = false
    var departmentCheck = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()

        initOnClickListener()

        onItemSelectSpinner()

        searchSchoolName.setOnClickListener(View.OnClickListener() {
            Log.d(TAG, "onCreate:  버튼 눌 ")
            if (schoolName.length() == 0) {
                Toast.makeText(this, "학교 이름을 입력 해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                var dialog = SchoolInfoDialog(this, schoolName.text.toString())
                dialog.showDialog()
                dialog.setOnClickListener(object : SchoolInfoDialog.OnDialogClickListener {
                    override fun onClicked(schoolInfo: schoolInfoData.SchoolInfo.Row) {
                        if (schoolInfo.HS_SC_NM.trim().equals("")) {
                            Toast.makeText(
                                applicationContext,
                                "현재 고등학교만 지원하고 있습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            schoolName.text.clear()
                            return
                        }
                        if (!schoolInfo.HS_SC_NM.equals("일반고")) {
                            var apiInterface =
                                ApiClient.getRetrofit().create(ApiInterface::class.java)
                            var call = apiInterface.getSchoolDepartmentInfo(
                                resources.getString(R.string.education_api_key),
                                "json",
                                1,
                                30,
                                resources.getString(R.string.area_code),
                                schoolInfo.SD_SCHUL_CODE
                            )
                            call.enqueue(object : Callback<SchoolDepartmentData> {
                                override fun onResponse(
                                    call: Call<SchoolDepartmentData>,
                                    response: Response<SchoolDepartmentData>
                                ) {
                                    if (response.isSuccessful) {
                                        Log.d(
                                            TAG,
                                            "onResponse: 학과 정보 불러옴 ${
                                                response.body()?.schoolMajorinfo?.get(1)?.row
                                            }}"
                                        )
                                        if (response.body()?.schoolMajorinfo?.get(1)?.row != null) {
                                            schoolDepartment.clear()
                                            for (i in response.body()?.schoolMajorinfo?.get(1)?.row!!) {
                                                schoolDepartment.add(i.DDDEP_NM)
                                                Log.d(TAG, "onResponse: ${i.DDDEP_NM} ")
                                            }
                                            Log.d(
                                                TAG,
                                                "onResponse: 리스트 확인 ${schoolDepartment.count()}"
                                            )
                                            schoolDepartmentSpinner.visibility = View.VISIBLE
                                            var dePartmentAdapter = ArrayAdapter<String>(
                                                applicationContext,
                                                android.R.layout.simple_list_item_1,
                                                schoolDepartment
                                            )
                                            schoolDepartmentSpinner.adapter = dePartmentAdapter
                                        }
                                    }
                                }


                                override fun onFailure(
                                    call: Call<SchoolDepartmentData>, t: Throwable
                                ) {
                                    Log.d(TAG, "onFailure: 학과 불러오기 실패 ${t.toString()}")
                                }

                            })
                            departmentCheck = false
                        }
                        schoolCheck = true
                        searchSchoolName.isClickable = false
                        searchSchoolName.setBackgroundColor(resources.getColor(R.color.gray))
                        schoolName.setBackgroundColor(resources.getColor(R.color.gray))
                        schoolName.setText(schoolInfo.SCHUL_NM)
                        selectSchoolInfo = schoolInfo
                        Log.d(
                            TAG,
                            "onClicked: 학교이름{${schoolInfo.SCHUL_NM}} 학교 코드 : {${schoolInfo.SD_SCHUL_CODE}} 계열명 : ${schoolInfo.HS_SC_NM}"
                        )
                    }
                })
            }
        })
        Log.d(TAG, "onCreate: ")


    }

    fun initView() {
        schoolName = findViewById<EditText>(R.id.school_name)
        searchSchoolName = findViewById<TextView>(R.id.search_button)

        schoolGradeSpinner = findViewById(R.id.school_Grade)
        schoolClassSpinner = findViewById(R.id.school_Class_Number)
        schoolDepartmentSpinner = findViewById<Spinner>(R.id.school_department)

        schoolGrade = resources.getStringArray(R.array.gradeList)
        schoolClassNumber = resources.getStringArray(R.array.classNum)
        schoolDepartment = ArrayList()

        schoolGradeAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, schoolGrade)
        schoolClassAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, schoolClassNumber)

        schoolGradeSpinner.adapter = schoolGradeAdapter
        schoolClassSpinner.adapter = schoolClassAdapter
        signInButton = findViewById(R.id.login_SignIn)
    }

    fun initOnClickListener() {
        schoolName.setOnClickListener(View.OnClickListener {
            schoolName.text.clear()
            schoolCheck = false
            searchSchoolName.isClickable = true
            searchSchoolName.setBackgroundColor(getColor(R.color.white))
            schoolName.setBackgroundColor(resources.getColor(R.color.white))
        })
        signInButton.setOnClickListener(View.OnClickListener {
            val checked = classCheck && gradeCheck && schoolCheck && departmentCheck
            Log.d(TAG, "다 체크되었는지 확인 ${checked} 학교 선택 ${schoolCheck} 학년 선택 ${gradeCheck} 학과 선택 ${departmentCheck} 반 선택 ${classCheck}")
            if(checked){
                val sharedPreferences = getSharedPreferences("Userinfo", MODE_PRIVATE)
                var editor = sharedPreferences.edit()
                editor.putString("schoolCode",selectSchoolInfo.SD_SCHUL_CODE)
                editor.putString("grade",gradeNumber)
                editor.putString("classNumber",classNumber)
                editor.putString("department",department)
                editor.putString("schoolName", selectSchoolInfo.SCHUL_NM)
                editor.putString("line",selectSchoolInfo.HS_SC_NM)
                editor.apply()

            }
        })
    }

    fun onItemSelectSpinner() {
        schoolClassSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                classNumber = schoolClassSpinner.getItemAtPosition(position).toString()
                Log.d(TAG, "onItemSelected: 반 선택 됨 ${classNumber}")
                classCheck = !classNumber.equals("반")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "onNothingSelected: 아무것도 선택되지 않음.")
            }
        }
        schoolGradeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                gradeNumber = schoolGradeSpinner.getItemAtPosition(position).toString()
                Log.d(TAG, "onItemSelected: 학년 선택 됨 ${gradeNumber}")
                gradeCheck = !gradeNumber.equals("학년")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "onNothingSelected: 아무것도 선택되지 않음.")
            }
        }
        schoolDepartmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                department = schoolDepartmentSpinner.getItemAtPosition(position).toString()
                departmentCheck = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "onNothingSelected: 아무것도 선택되지 않음.")
            }
        }
    }
}