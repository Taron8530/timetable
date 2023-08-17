package com.example.timetable

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class ProfileFragment(var schoolInfo : SchoolInfo) : Fragment() {
    private lateinit var root : View
    private lateinit var editProfile : Button
    private lateinit var mealSetting : androidx.appcompat.widget.SwitchCompat
    private lateinit var mealSettingSharedPreferences: SharedPreferences
    private lateinit var mealSettingEditor : SharedPreferences.Editor
    private var mealSettingChecked : Boolean = false
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
    @SuppressLint("CommitPrefEdits")
    fun init(){
        editProfile = root.findViewById(R.id.profile_edit_school)
        mealSetting = root.findViewById(R.id.mealSetting)
        mealSettingSharedPreferences = activity?.getSharedPreferences("Userinfo", AppCompatActivity.MODE_PRIVATE)!!
        mealSettingChecked = mealSettingSharedPreferences.getBoolean("homeMealSetting",false)
        mealSettingEditor = mealSettingSharedPreferences.edit()
        mealSetting.isChecked = mealSettingChecked
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
        mealSetting.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                Log.d(TAG, "onCheckedChanged: ${isChecked}")
                mealSettingEditor.putBoolean("homeMealSetting",isChecked)
                mealSettingEditor.commit()
            }

        })
    }
}