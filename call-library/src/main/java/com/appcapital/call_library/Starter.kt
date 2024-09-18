package com.appcapital.call_library


import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.appcapital.call_library.aftercall.AfterCallActivity
import com.appcapital.call_library.config.AfterCallConfig
import com.appcapital.call_library.service.PhoneCallService
import com.appcapital.call_library.utils.SharedPreferencesHelper

class Starter(appConfig: AfterCallConfig,context: Context) {
   private val saveConfig = SharedPreferencesHelper
       .saveAppConfig(context,appConfig.packageName,appConfig.classEntryName,appConfig.appName,appConfig.appIcon,appConfig.primaryColor, appConfig.secondaryColor, appConfig.customView)
    init {
        val serviceIntent = Intent(context, PhoneCallService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
fun displayAfterCallScreen(context: Context){
    val intent: Intent = Intent(context, AfterCallActivity::class.java)
    context.startActivity(intent)
}

}