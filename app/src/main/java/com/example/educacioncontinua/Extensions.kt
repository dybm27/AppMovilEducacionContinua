package com.example.educacioncontinua

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Activity.toast(msg:String,time:Int = Toast.LENGTH_SHORT){
    Toast.makeText(this,msg,time).show()
}

fun Fragment.toast(msg:String,time:Int = Toast.LENGTH_SHORT){
    Toast.makeText(this.context,msg,time).show()
}