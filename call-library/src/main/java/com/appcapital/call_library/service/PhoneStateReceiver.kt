package com.appcapital.call_library.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat.startActivity
import com.appcapital.call_library.MainActivity
import com.appcapital.call_library.aftercall.AfterCallActivity
import com.appcapital.call_library.utils.SharedPreferencesHelper
import com.appcapital.call_library.utils.Utils


class PhoneStateReceiver : BroadcastReceiver() {
    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var callStartTime: Long = 0
    var callPhoneNumber = "";
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            callPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER) ?: ""
            SharedPreferencesHelper.saveCalledPhoneNumber(context,callPhoneNumber)
            Log.d("PhoneCallReceiver", "Phone number 0: $callPhoneNumber")
        } else {
            val telephonyManager =
                context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.listen(
                object : PhoneStateListener() {
                    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                        super.onCallStateChanged(state, phoneNumber)
                        if (lastState == state) {
                            return
                        }

                        when (state) {
                            TelephonyManager.CALL_STATE_RINGING -> {
                                Log.d("PhoneCallReceiver", "Incoming call ringing")
                            }
                            TelephonyManager.CALL_STATE_OFFHOOK -> {
                                Log.d("PhoneCallReceiver", "Call taken")
                                callStartTime = System.currentTimeMillis()
                            }
                            TelephonyManager.CALL_STATE_IDLE -> {
                                Log.d("PhoneCallReceiver", "Call ended")
//                                val intent = Intent(context, MainActivity::class.java).apply {
//                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                }
//                                appContext?.startActivity(intent)
                                val callDuration = System.currentTimeMillis() - callStartTime
                                Log.d("PhoneCallReceiver", "Phone number: $callPhoneNumber --- $phoneNumber")
                                // Pass the call duration to AfterCallActivity
                                val dialogIntent = Intent(context, AfterCallActivity::class.java).apply {
                                  putExtra("CALL_DURATION", Utils.formatMillisecondsToTime(callDuration))
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                Handler(Looper.getMainLooper()).postDelayed({
                                    context.startActivity(dialogIntent)
                                }, 3000)
                            }
                        }

                        lastState = state
                    }
                },
                PhoneStateListener.LISTEN_CALL_STATE
            )
        }

    }

}