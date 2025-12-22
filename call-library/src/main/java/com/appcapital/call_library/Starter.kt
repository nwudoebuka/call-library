package com.appcapital.call_library


import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.appcapital.call_library.aftercall.AfterCallActivity
import com.appcapital.call_library.config.AfterCallConfig
import com.appcapital.call_library.newdesign.NewAfterCallActivity
import com.appcapital.call_library.newdesign.prominentdisclosure.ProminentDisclosureActivity
import com.appcapital.call_library.service.CallMonitorService
import com.appcapital.call_library.service.PhoneCallService
import com.appcapital.call_library.utils.SharedPreferencesHelper

class Starter(appConfig: AfterCallConfig,context: Context) {
   private val saveConfig = SharedPreferencesHelper
       .saveAppConfig(context,appConfig.packageName,appConfig.classEntryName,appConfig.appName,appConfig.appIcon,appConfig.primaryColor, appConfig.secondaryColor, appConfig.customView)
    init {
        val serviceIntent = Intent(context, PhoneCallService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)


        val intent = Intent(context, CallMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
fun displayAfterCallScreen(context: Context){
    val intent: Intent = Intent(context, ProminentDisclosureActivity::class.java)
    context.startActivity(intent)
}

}