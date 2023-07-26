package com.example.timetable

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast

class DialogMessage (var context : Context){
    fun showMessage(title : String,message : String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.show()
    }
}