package com.appcapital.call_library.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.WindowManager
import android.os.Build
import androidx.core.content.ContextCompat.startActivity
import com.appcapital.call_library.MainActivity
import com.appcapital.call_library.aftercall.AfterCallActivity
import com.appcapital.call_library.utils.SharedPreferencesHelper
import com.appcapital.call_library.utils.Utils
import com.appcapital.call_library.R
import com.appcapital.call_library.aftercall.WeatherCardFragment


class PhoneStateReceiver : BroadcastReceiver() {
    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var callStartTime: Long = 0
    private var isIncoming = false
    private var callPhoneNumber = ""
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        if (intent.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            // Outgoing call
            callPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER) ?: ""
            SharedPreferencesHelper.saveCalledPhoneNumber(context, callPhoneNumber)
            isIncoming = false
            Log.d("PhoneCallReceiver", "Outgoing call to: $callPhoneNumber")

        } else {
          phoneState(context,intent)
        }
    }
    private fun phoneState(context: Context?, intent: Intent?){
        val telephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                super.onCallStateChanged(state, phoneNumber)

                // If the state hasn't changed, do nothing
                if (lastState == state) return

                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        // Incoming call is ringing
                        isIncoming = true
                        callPhoneNumber = phoneNumber ?: ""
                        Log.d("PhoneCallReceiver", "Incoming call from: $callPhoneNumber")
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        // Call is answered (either incoming or outgoing)
                        callStartTime = System.currentTimeMillis()
                        Log.d("PhoneCallReceiver", "Call started, isIncoming: $isIncoming")
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        // Call ended
                        if (lastState == TelephonyManager.CALL_STATE_OFFHOOK) {
                            val callDuration = System.currentTimeMillis() - callStartTime
                            Log.d("PhoneCallReceiver", "Call ended. Duration: ${callDuration / 1000} seconds")

                            // Launch AfterCallActivity
//                            val dialogIntent = Intent(context, AfterCallActivity::class.java).apply {
//                                putExtra("CALL_DURATION", Utils.formatMillisecondsToTime(callDuration))
//                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                //context?.startActivity(dialogIntent)
                                val appContext = context.applicationContext
//                                renderOverLayAfterCallView(appContext)
                            }, 3000)
                        }
                    }
                }
                lastState = state
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun renderOverLayAfterCallView(context: Context){
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )


        val view = LayoutInflater.from(context).inflate(R.layout.activity_after_call, null)
        windowManager.addView(view, params)
    }
}