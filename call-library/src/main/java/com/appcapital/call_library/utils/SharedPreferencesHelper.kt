package com.appcapital.call_library.utils

import android.content.Context
import android.content.SharedPreferences
import com.appcapital.call_library.config.AfterCallConfig

object SharedPreferencesHelper {
    private const val PREF_FILE_NAME = "call_sdk_preferences"

    // Example keys for configuration values
    private const val KEY_PACKAGE_NAME = "package_name"
    private const val KEY_CLASS_ENTRY_NAME = "package_name"
    private const val KEY_APP_NAME = "app_name"
    private const val KEY_APP_ICON = "app_icon"
    private const val KEY_PRIMARY_COLOR = "primary_color"
    private const val KEY_SECONDARY_COLOR = "secondary_color"

    // Function to get SharedPreferences instance
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    // Function to save configuration values to SharedPreferences
    fun saveAppConfig(context: Context, packageName: String, classEntryName: String,appName: String, appIcon: Int, primaryColor: Int, secondaryColor: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_PACKAGE_NAME,packageName)
        editor.putString(KEY_CLASS_ENTRY_NAME,classEntryName)
        editor.putString(KEY_APP_NAME, appName)
        editor.putInt(KEY_APP_ICON, appIcon)
        editor.putInt(KEY_PRIMARY_COLOR, primaryColor)
        editor.putInt(KEY_SECONDARY_COLOR, secondaryColor)
        editor.apply()
    }

    // Function to retrieve configuration values from SharedPreferences
    fun getAppConfig(context: Context): AfterCallConfig {
        val sharedPreferences = getSharedPreferences(context)
        return AfterCallConfig(
            sharedPreferences.getString(KEY_PACKAGE_NAME, "") ?: "",
            sharedPreferences.getString(KEY_CLASS_ENTRY_NAME, "") ?: "",
            sharedPreferences.getString(KEY_APP_NAME, "") ?: "",
            sharedPreferences.getInt(KEY_APP_ICON, 0),
            sharedPreferences.getInt(KEY_PRIMARY_COLOR, 0),
            sharedPreferences.getInt(KEY_SECONDARY_COLOR, 0)
        )
    }

}