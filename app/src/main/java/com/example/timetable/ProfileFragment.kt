package com.example.timetable

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ProfileFragment(var schoolInfo : SchoolInfo) : Fragment() {
    private lateinit var root : View
    private lateinit var editProfile : Button
    private val TAG = "ProfileFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_profile, container, false)
        // Inflate the layout for this fragment
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setListener()

    }
    // view 초기화 하는 메서드
    fun init(){
        editProfile = root.findViewById(R.id.profile_edit_school)
    }
    fun setListener(){
        editProfile.setOnClickListener(object  : View.OnClickListener{
            override fun onClick(v: View?) {
                val sharedPreferences = activity?.getSharedPreferences("Userinfo",0)
                val editor = sharedPreferences?.edit()
                Log.d(TAG, "onClick: "+ editor?.clear())
                editor?.clear()
                if(editor?.commit() == true){
                    val intent = Intent(context,LoginActivity :: class.java)
                    startActivity(intent)
                }

            }

        })
    }
}