package com.appcapital.phonecallsdk

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.appcapital.call_library.Starter
import com.appcapital.call_library.config.AfterCallConfig

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val customView = layoutInflater.inflate(R.layout.call_layout, null)
        val afterCallConfig = AfterCallConfig(
            "screen.recorder.capture.video.record",
            "screen.recorder.capture.video.record.ui.MainActivity",
            appIcon = R.drawable.ic_notification,
            appName = "Scientific Calculator",
            primaryColor = R.color.green,
            secondaryColor = R.color.green,
            customView = R.layout.call_layout
        )
        setContentView(R.layout.activity_main)
        requestPermissions()
        Starter(afterCallConfig,this).displayAfterCallScreen(this);
    }
    private fun requestPermissions(){
        // TODO this should go to like OPTIN flow
        if (applicationContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                1000
            )
        }
        if (applicationContext.checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS),
                2000
            )
        }

        if (applicationContext.checkSelfPermission(Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                3000
            )
        }
        if (applicationContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                4000)
        }
    }
}