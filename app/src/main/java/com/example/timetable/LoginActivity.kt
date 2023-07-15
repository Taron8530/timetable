package com.example.timetable

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    var TAG = "LoginActivity"
    lateinit var schoolName : EditText
    lateinit var searchSchoolName: TextView
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
                            var result: String = response.body()?.schoolInfo?.get(1)?.row.toString()
                            Log.d(TAG, "onResponse 성공: " + result);
                            Log.d(TAG, "onResponse:111 "+test)
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