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
import com.appcapital.call_library.aftercall.AfterCallActivity
import com.appcapital.call_library.newdesign.AfterCallWindow
import java.util.Locale
import java.util.concurrent.TimeUnit

class CallMonitorService : Service() {
    private val CHANNEL_ID = "CallMonitorServiceChannel"
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: PhoneStateListener

    private var lastCapturedNumber: String? = null

    private val TAG: String = PhoneStateReceiver::class.simpleName.toString()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createForegroundNotification()
        startForeground(1, notification)

        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        // Using the deprecated method to support older Android versions reliably
        @Suppress("DEPRECATION")
        phoneStateListener = object : PhoneStateListener() {
            private var wasInCall = false // Tracks if a call was previously active
            private var lastCapturedNumber: String? = null // Store the number here

            private var callStartTime: Long = 0


            override fun onCallStateChanged(state: Int, incomingNumber: String?) {
                super.onCallStateChanged(state, incomingNumber)
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING, TelephonyManager.CALL_STATE_OFFHOOK -> {
                        // Call started (incoming) or is active (off-hook)
                        wasInCall = true
                        callStartTime = System.currentTimeMillis()
                        if (!incomingNumber.isNullOrEmpty()) {
                            lastCapturedNumber = incomingNumber
                        }

                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        Log.d(TAG, "${lastCapturedNumber} Call has ended (IDLE)")
                        // Call ended
                        if (wasInCall) {
                            val callEndTime = System.currentTimeMillis()
                            val durationMillis = callEndTime - callStartTime

                            // Format duration to mm:ss
                            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
                            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
                            val durationString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                            val finalNumber = lastCapturedNumber ?: "Unknown"

                            Log.d(TAG, "Number: $finalNumber")
                            Log.d(TAG, "Duration: $durationString ($durationMillis ms)")

                            wasInCall = false
                            // Display the overlay screen
                            launchAfterCallActivity()
                            lastCapturedNumber = null
                            callStartTime = 0
                        }
                    }
                }
            }
        }

        // Register the listener to monitor call state changes
        @Suppress("DEPRECATION")
        try {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception){

        }
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

    private fun launchAfterCallActivity() {
        val afterCallWindow = AfterCallWindow(this.applicationContext)
        afterCallWindow.show()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Return START_STICKY to ensure the service is restarted by the system if it gets terminated.
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CallMonitorService", "Service destroyed. Unregistering listener.")
        try {
            @Suppress("DEPRECATION")
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        } catch (e: Exception) {
            // Ignore exceptions during teardown
        }
        scheduleServiceRestart(applicationContext)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null // Not a bound service
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Call Monitor Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createForegroundNotification(): Notification {
        val notificationIntent = Intent(this, AfterCallActivity::class.java) // Use an activity in the host app if needed
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("After Call Service")
            .setContentText("Phone Dial Assistant")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .build()
    }
}