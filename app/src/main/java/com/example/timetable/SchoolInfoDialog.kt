package com.example.timetable

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SchoolInfoDialog(private val context : Context,private var schoolName : String,private var schoolOfficeCode: String) {
    private val dialog = Dialog(context)
    private val TAG = "SchoolInfoDialog"
    private lateinit var schoolInfo : RecyclerView
    private lateinit var onClickListener : OnDialogClickListener
    private lateinit var schoolInfoAdapter : SchoolInfoAdapter
    private var data = mutableListOf<schoolInfoData.SchoolInfo.Row>()
    lateinit var progressBar : ProgressBar
    lateinit var progressBarComment : TextView
    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }
    fun showDialog(){
        initView()
        getSchoolInfo()

    }
    fun initView(){
        dialog.setContentView(R.layout.school_info_dialog)
        schoolInfo = dialog.findViewById(R.id.school_info_recyclerview)
        schoolInfoAdapter = SchoolInfoAdapter(context,data)
        schoolInfoAdapter.setOnClickListener(object : SchoolInfoAdapter.OnclickListener{
            override fun onclick(position: Int) {
                onClickListener.onClicked(data.get(position))
                dialog.dismiss()
            }


        })
        progressBarComment = dialog.findViewById(R.id.school_progressBar_comment)
        progressBar = dialog.findViewById(R.id.school_progressBar)
        showProgress(true)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()
    }
    fun getSchoolInfo(){
        var Api : ApiInterface = ApiClient.getRetrofit().create(ApiInterface::class.java)
        var call : Call<schoolInfoData> = Api.getSchoolInfo(
            context.resources.getString(R.string.education_api_key),
            "JSON",
            1,
            40,
            schoolOfficeCode,
            schoolName
        )
        call?.enqueue(object : Callback<schoolInfoData> {
            override fun onResponse(call: Call<schoolInfoData>, response: Response<schoolInfoData>) {
                if(response.isSuccessful){
                    // 정상적으로 통신이 성고된 경우
                    var test: String = response.body()?.schoolInfo.toString()
                    if(response.body()?.schoolInfo == null){
                        Toast.makeText(context,"검색결과가 없습니다", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "onResponse: ${response.body()?.schoolInfo.toString()}")
                        dialog.dismiss()
                    }else{
                        var result: MutableList<schoolInfoData.SchoolInfo.Row> = response.body()?.schoolInfo?.get(1)?.row!!.toList() as MutableList<schoolInfoData.SchoolInfo.Row>
                        Log.d(TAG, "onResponse 성공: " + result);
                        Log.d(TAG, "onResponse:111 "+result[0].SCHUL_NM)
                        schoolInfo.setHasFixedSize(true)
                        schoolInfo.setLayoutManager(LinearLayoutManager(context))
                        // RecyclerView 에 Adapter 를 할당합니다.
                        schoolInfo.adapter = schoolInfoAdapter
                        schoolInfoAdapter.setList(result)
                        data = result
                        deleteExceptionSchool()
                        showProgress(false)
                        schoolInfoAdapter.notifyDataSetChanged()
                    }


                }else{
                    showProgress(false)
                    Toast.makeText(context,"네트워크 환경을 확인하여 주십시오.",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d(TAG, "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<schoolInfoData>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                showProgress(false)
                dialog.dismiss()
                Toast.makeText(context,"네트워크 환경을 확인하여 주십시오.",Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onFailure 에러: " + t.message.toString());
            }
        })
    }
    fun deleteExceptionSchool(){ // 중학교와 초등학교를 제외시키는 코드
        var removeItemList = ArrayList<schoolInfoData.SchoolInfo.Row>()
        for (i in data){
            if(i.SCHUL_NM.endsWith("중학교") || i.SCHUL_NM.endsWith("초등학교")){
                removeItemList.add(i)
            }
        }
        data.removeAll(removeItemList)
        if(data.size <= 0){
            Toast.makeText(context,"검색 결과가 없습니다. 현재 고등학교만 지원하고 있습니다.",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        schoolInfoAdapter.notifyDataSetChanged()
    }
    interface OnDialogClickListener
    {
        fun onClicked(schoolInfo : schoolInfoData.SchoolInfo.Row)
    }
    fun showProgress(isShow : Boolean){
        if(isShow){
            progressBar.visibility = View.VISIBLE
            progressBarComment.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.GONE
            progressBarComment.visibility = View.GONE
        }
    }
}