package com.appcapital.phonecallsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appcapital.call_library.Starter
import com.appcapital.call_library.config.AfterCallConfig

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val afterCallConfig = AfterCallConfig(
            "screen.recorder.capture.video.record",
            "screen.recorder.capture.video.record.ui.MainActivity",
            appIcon = R.drawable.ic_notification,
            appName = "Scientific Calculator",
            primaryColor = R.color.green,
            secondaryColor = R.color.green
        )
        setContentView(R.layout.activity_main)
        Starter(afterCallConfig,this).displayAfterCallScreen(this);
    }
}