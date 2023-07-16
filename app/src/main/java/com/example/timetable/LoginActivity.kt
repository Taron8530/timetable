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
                var Api : ApiInterface = ApiClient.getRetrofit().create(ApiInterface::class.java)
                var call : Call<schoolInfoData> = Api.getSchoolInfo(
                    AplicationKey.APIKEY,
                    "JSON",
                    1,
                    10,
                    "J10",
                    schoolName.text.toString()
                )
                call?.enqueue(object : Callback<schoolInfoData>{
                    override fun onResponse(call: Call<schoolInfoData>, response: Response<schoolInfoData>) {
                        if(response.isSuccessful){
                            // 정상적으로 통신이 성고된 경우
                            var test: String = response.body()?.schoolInfo.toString()
                            if(response.body()?.schoolInfo == null){
                                Toast.makeText(applicationContext,"검색결과가 없습니다",Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "onResponse: ${response.body()?.schoolInfo.toString()}")
                            }else{
                                var result: List<schoolInfoData.SchoolInfo.Row> = response.body()?.schoolInfo?.get(1)?.row!!.toList()
                                Log.d(TAG, "onResponse 성공: " + result);
                                Log.d(TAG, "onResponse:111 "+result[0].SCHUL_NM)
                                var adapter = SchoolInfoAdapter(applicationContext, result)
                                schoolInfoRecyclerView = findViewById(R.id.schoolInfoList)
                                schoolInfoRecyclerView.setHasFixedSize(true)
                                schoolInfoRecyclerView.setLayoutManager(LinearLayoutManager(applicationContext))
                                // RecyclerView 에 Adapter 를 할당합니다.
                                schoolInfoRecyclerView.adapter = adapter
                                adapter.notifyDataSetChanged()
                            }


                        }else{
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d(TAG, "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<schoolInfoData>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d(TAG, "onFailure 에러: " + t.message.toString());
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