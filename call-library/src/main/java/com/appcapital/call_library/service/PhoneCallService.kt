package com.appcapital.call_library.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.appcapital.call_library.newdesign.AfterCallWindow

class PhoneCallService : Service() {

    private lateinit var telephonyManager: TelephonyManager
    private var phoneStateListener: PhoneStateListener? = null
    private val TAG: String = PhoneStateReceiver::class.simpleName.toString()
    private var wasOffhook = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"Command start")
        startForegroundServiceWithNotification()
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        // Call is active (either outgoing or answered)
                        Log.d(TAG, "Call is offhook (active)")
                        wasOffhook = true
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        // Phone is idle, meaning the call has ended
                        if (wasOffhook) {
                            Log.d(TAG, "${phoneNumber} Call has ended (IDLE)")
                            launchAfterCallActivity()
                            wasOffhook = false // Reset for the next call
                        }
                    }
                    TelephonyManager.CALL_STATE_RINGING -> {
                        // Phone is ringing (incoming call)
                        Log.d(TAG, "Phone is ringing (incoming call)")
                    }
                }
            }
        }
        try {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception){

        }

        return START_STICKY
    }


    private fun startForegroundServiceWithNotification() {
        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "phone_call_service_channel"
            val channelName = "Phone Call Service"
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Create a persistent notification for the foreground service
        val notification: Notification = NotificationCompat.Builder(this, "phone_call_service_channel")
            .setContentTitle("Phone Call Service")
            .setContentText("Listening for phone call states")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        // Call startForeground() with the notification
        startForeground(1, notification)
    }
    private fun launchAfterCallActivity() {
        val afterCallWindow = AfterCallWindow(this.applicationContext)
        afterCallWindow.show()
    }

    /**
     * Schedules a restart of the CallMonitorService using AlarmManager.
     */
    internal fun scheduleServiceRestart(context: Context) {

        val OVERLAY_PERMISSION_REQUEST_CODE = 1001
        // Interval for attempting service restart after an unexpected kill (e.g., 5 seconds)
        val SERVICE_RESTART_DELAY_MS = 5000L

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RestartServiceReceiver::class.java)

        // Use a unique request code and FLAG_IMMUTABLE/FLAG_UPDATE_CURRENT for safety
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = System.currentTimeMillis() + SERVICE_RESTART_DELAY_MS

        // Attempt to set a slightly more battery-efficient alarm for older devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
        Log.i("AdCallOverlaySDK", "Scheduled service restart in $SERVICE_RESTART_DELAY_MS ms.")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the PhoneStateListener to prevent leaks
        try {
            phoneStateListener?.let {
                telephonyManager.listen(it, PhoneStateListener.LISTEN_NONE)
            }
            phoneStateListener = null
            Log.d(TAG, "Service destroyed, listener unregistered")
            Log.d("CallMonitorService", "Context is null")
            scheduleServiceRestart(this)

        } catch (e: Exception){

        }
    }
    override fun onBind(intent: Intent?): IBinder? = null
}

