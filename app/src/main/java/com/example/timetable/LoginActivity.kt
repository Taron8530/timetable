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
    lateinit var schoolOfficeAdapter : ArrayAdapter<String>

    lateinit var schoolGradeSpinner: Spinner
    lateinit var schoolClassSpinner: Spinner
    lateinit var schoolDepartmentSpinner : Spinner
    lateinit var schoolOfficeSpinner : Spinner

    lateinit var schoolGrade: Array<String>
    lateinit var schoolClassNumber: Array<String>
    val schoolOfficeCodeArray= hashMapOf("서울특별시 교육청" to "B10", "경기도 교육청" to "J10", "부산광역시 교육청" to "C10", "대구광역시 교육청" to "D10","인천광역시 교육청" to "E10","광주광역시 교육청" to "F10","대전광역시 교육청" to "G10","울산광역시 교육청" to "H10","세종특별자치시 교육청" to "E10","강원특별자치도교육청" to "K10","충청북도 교육청" to "M10","충청남도 교육청" to "N10","전라북도 교육청" to "P10","전라남도 교육청" to "Q10","경상북도 교육청" to "R10","경상남도 교육청" to "S10","제주특별자치도교육청" to "T10")

    lateinit var selectSchoolInfo: schoolInfoData.SchoolInfo.Row
    lateinit var schoolDepartment: ArrayList<String>
    lateinit var signInButton: Button

    lateinit var classNumber: String
    lateinit var gradeNumber: String
    lateinit var schoolOfficeCode : String
    var department: String = ""
    var schoolCheck = false
    var gradeCheck = false
    var classCheck = false
    var schoolOfficeCodeCheck = false
    var departmentCheck = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkLogin()

        initView()

        initOnClickListener()

        onItemSelectSpinner()

        Log.d(TAG, "onCreate: ")


    }

    fun initView() {
        schoolName = findViewById<EditText>(R.id.school_name)
        searchSchoolName = findViewById<TextView>(R.id.search_button)

        schoolGradeSpinner = findViewById(R.id.school_Grade)
        schoolClassSpinner = findViewById(R.id.school_Class_Number)
        schoolDepartmentSpinner = findViewById<Spinner>(R.id.school_department)
        schoolOfficeSpinner = findViewById(R.id.school_office_code)

        schoolGrade = resources.getStringArray(R.array.gradeList)
        schoolClassNumber = resources.getStringArray(R.array.classNum)
        schoolDepartment = ArrayList()

        schoolGradeAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, schoolGrade)
        schoolClassAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, schoolClassNumber)
        schoolOfficeAdapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, getSchoolOfficeName())

        schoolGradeSpinner.adapter = schoolGradeAdapter
        schoolClassSpinner.adapter = schoolClassAdapter
        schoolOfficeSpinner.adapter = schoolOfficeAdapter
        signInButton = findViewById(R.id.login_SignIn)
    }


    fun initOnClickListener() {
        searchSchoolName.setOnClickListener(View.OnClickListener() {
            Log.d(TAG, "onCreate:  버튼 눌 ")
            if(!schoolOfficeCodeCheck){
                Toast.makeText(this, "교육청을 먼저 선택해야 합니다!", Toast.LENGTH_SHORT).show()
            } else if (schoolName.length() == 0) {
                Toast.makeText(this, "학교 이름을 입력 해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                var dialog = SchoolInfoDialog(this, schoolName.text.toString(),schoolOfficeCode)
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
                                schoolOfficeCode,
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

                        schoolOfficeSpinner.setBackgroundColor(resources.getColor(R.color.gray))
                        schoolOfficeSpinner.isClickable = false
                        Log.d(
                            TAG,
                            "onClicked: 학교이름{${schoolInfo.SCHUL_NM}} 학교 코드 : {${schoolInfo.SD_SCHUL_CODE}} 계열명 : ${schoolInfo.HS_SC_NM}"
                        )
                    }
                })
            }
        })
        schoolName.setOnClickListener(View.OnClickListener {
            schoolName.text.clear()
            schoolCheck = false
            searchSchoolName.isClickable = true
            searchSchoolName.setBackgroundColor(getColor(R.color.white))
            schoolName.setBackgroundColor(resources.getColor(R.color.white))
            schoolDepartmentSpinner.visibility = View.GONE

            schoolOfficeSpinner.setBackgroundColor(resources.getColor(R.color.white))
            schoolOfficeSpinner.isClickable = true
            schoolOfficeCodeCheck = false
        })
        signInButton.setOnClickListener(View.OnClickListener {
            val checked = classCheck && gradeCheck && schoolCheck && departmentCheck
            val dialogMessage = DialogMessage(applicationContext)
            Log.d(TAG, "다 체크되었는지 확인 ${checked} 학교 선택 ${schoolCheck} 학년 선택 ${gradeCheck} 학과 선택 ${departmentCheck} 반 선택 ${classCheck}")
            if(checked) {
                val sharedPreferences = getSharedPreferences("Userinfo", MODE_PRIVATE)
                var editor = sharedPreferences.edit()
                editor.putString("schoolCode", selectSchoolInfo.SD_SCHUL_CODE)
                editor.putString("grade", gradeNumber)
                editor.putString("classNumber", classNumber)
                editor.putString("department", department)
                editor.putString("schoolName", selectSchoolInfo.SCHUL_NM)
                editor.putString("schoolOfficeCode",schoolOfficeCode)
                editor.putString("line", selectSchoolInfo.HS_SC_NM)
                if (editor.commit()) {
                    var intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }else if(!schoolCheck){
                Toast.makeText(applicationContext, "학교를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }else if(!departmentCheck){
                Toast.makeText(applicationContext, "학과를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }else if(!classCheck){
                Toast.makeText(applicationContext, "반을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }else if(!gradeCheck){
                Toast.makeText(applicationContext, "학년을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        })
    }



    fun onItemSelectSpinner() {
        schoolOfficeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                schoolOfficeCode = schoolOfficeSpinner.getItemAtPosition(position).toString()
                Log.d(TAG, "onItemSelected0: ${schoolOfficeCode} check :${schoolOfficeCodeCheck}")
                schoolOfficeCodeCheck = !schoolOfficeCode.equals("교육청 선택")
                if(schoolOfficeCodeCheck){
                    schoolOfficeCode = schoolOfficeCodeArray[schoolOfficeCode].toString()
                }
                Log.d(TAG, "onItemSelected1: ${schoolOfficeCode} check :${schoolOfficeCodeCheck}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
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
    fun getSchoolOfficeName() : ArrayList<String>{
        var array : ArrayList<String> = ArrayList()
        array.add("교육청 선택")
        for(i in schoolOfficeCodeArray.keys) {
            array.add(i)
        }
        return array
    }


    fun checkLogin(){
        var sharedPreferences = getSharedPreferences("Userinfo",0)
        var schoolCode = sharedPreferences.getString("schoolCode","")
        if(!schoolCode.equals("")){
            for (i in schoolOfficeCodeArray.keys) {
                Log.d(TAG, "checkLogin: 전체 : ${i}")
            }
            var intent = Intent(applicationContext,MainActivity ::class.java)
            startActivity(intent)
        }
    }
}