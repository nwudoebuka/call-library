package com.appcapital.call_library.config

import android.app.Activity

data class AfterCallConfig(
    val packageName: String,
    val classEntryName: String,
    val appName: String,
    val appIcon: Int,
    val primaryColor: Int,
    val secondaryColor: Int
)