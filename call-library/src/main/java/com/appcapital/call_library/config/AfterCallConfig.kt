package com.appcapital.call_library.config

import android.app.Activity
import android.view.View

data class AfterCallConfig(
    val packageName: String,
    val classEntryName: String,
    val appName: String,
    val appIcon: Int,
    val primaryColor: Int,
    val secondaryColor: Int,
    val customView: Int
)