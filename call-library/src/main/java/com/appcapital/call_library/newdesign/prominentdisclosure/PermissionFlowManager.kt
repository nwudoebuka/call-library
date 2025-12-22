package com.appcapital.call_library.newdesign.prominentdisclosure

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.appcapital.call_library.R

class PermissionFlowManager(
    private val activity: Activity,
    private val onComplete: () -> Unit
) {

    private val overlay = PermissionOverlayWindow(
        activity,
        onAllow = { requestCurrentPermission() },
        onSkip = { skipCurrentPermission() }
    )

    private val permissions = permissionQueue.toMutableList()
    private var current: PermissionConfig? = null

    fun start() {
        showNext()
    }

    private fun showNext() {
        if (permissions.isEmpty()) {
            onComplete()
            return
        }


        current = permissions.removeAt(0)

        if (ContextCompat.checkSelfPermission(
                activity,
                current!!.permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showNext()
        } else {
            current?.let {
                overlay.show(it)
            }

        }
    }

    fun onRemove() {
        overlay.remove()
    }

    private fun skipCurrentPermission() {
        showNext()
    }

    private fun requestCurrentPermission() {
        current?.let {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(it.permission),
                2001
            )
        }
    }

    fun onPermissionResult() {
        showNext()
    }

}

data class PermissionConfig(
    val permission: String,
    val title: String,
    val description: String,
    val iconRes: Int
)

val permissionQueue = listOfNotNull(
    PermissionConfig(
        Manifest.permission.ACCESS_FINE_LOCATION,
        "Would you like to \nAllow Location Access",
        "Start to share your location with us",
        R.drawable.ic_location_on
    ),
    PermissionConfig(
        Manifest.permission.READ_PHONE_STATE,
        "Would you like to \nAllow Call Log Access",
        "To get started allow us access your phone logs",
        R.drawable.ic_add_contact
    ),
    PermissionConfig(
        Manifest.permission.POST_NOTIFICATIONS,
        "Would you like to \nEnable Notifications",
        "Stay up to date with alerts",
        R.drawable.ic_notif
    ),
    PermissionConfig(
        Manifest.permission.READ_CONTACTS,
        "Would you like to \nAllow Contacts Access",
        "To get started allow us access your phone logs",
        R.drawable.ic_profile
    ),

    PermissionConfig(
        Manifest.permission.READ_CALL_LOG,
        "Would you like to allow access to Call Logs",
        "Get Customised features",
        R.drawable.ic_add_contact
    ),
)



