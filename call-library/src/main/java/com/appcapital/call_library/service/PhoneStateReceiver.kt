package com.appcapital.call_library.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.appcapital.call_library.MainActivity


class PhoneStateReceiver : BroadcastReceiver() {
    private var lastState = TelephonyManager.CALL_STATE_IDLE
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            val phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            Log.d("PhoneCallReceiver", "Outgoing call: $phoneNumber")
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
                            }
                            TelephonyManager.CALL_STATE_IDLE -> {
                                Log.d("PhoneCallReceiver", "Call ended")
//                                val intent = Intent(context, MainActivity::class.java).apply {
//                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                }
//                                appContext?.startActivity(intent)
                                val dialogIntent = Intent(
                                    context,
                                    MainActivity::class.java
                                )
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(dialogIntent)
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