package com.example.timetable

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast

class DialogMessage (var context : Context){
    val builder = AlertDialog.Builder(context)
    fun showMessage(title : String,message : String){
        builder.setTitle(title)
        builder.setMessage(message)
        builder.show()
    }
}