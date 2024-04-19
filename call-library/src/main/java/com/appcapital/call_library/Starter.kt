package com.appcapital.call_library


import android.content.Context
import android.content.Intent
import android.util.Log
import com.appcapital.call_library.config.AfterCallConfig
import com.appcapital.call_library.service.PhoneStateReceiver
import com.appcapital.call_library.utils.SharedPreferencesHelper

class Starter(appConfig: AfterCallConfig,context: Context) {
   private val saveConfig = SharedPreferencesHelper
       .saveAppConfig(context,appConfig.packageName,appConfig.classEntryName,appConfig.appName,appConfig.appIcon,appConfig.primaryColor,appConfig.secondaryColor)
    init {

    }
fun displayAfterCallScreen(context: Context){
//    val intent: Intent = Intent(context, MainActivity::class.java)
//    context.startActivity(intent)
}

}