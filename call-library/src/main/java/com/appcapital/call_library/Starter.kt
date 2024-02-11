package com.appcapital.call_library


import android.content.Context
import android.content.Intent
import android.util.Log

class Starter(appName: String) {
    private val TAG = "APP_NAME"
    init {
        Log.i(TAG,appName)

    }
fun displayAfterCallScreen(context: Context){
    val intent: Intent = Intent(context, MainActivity::class.java)
    context.startActivity(intent)
}

}