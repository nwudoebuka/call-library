package com.appcapital.call_library.newdesign.prominentdisclosure

import android.Manifest
import android.os.Build
import androidx.lifecycle.ViewModel
import com.appcapital.call_library.R

class ProminentDisclosureViewModel : ViewModel() {

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
            Manifest.permission.READ_SMS,
            "Would you like to \nAllow SMS Access",
            "For tailored effect allow us access your phone sms for quick response",
            R.drawable.ic_message
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
        )
    )
}
