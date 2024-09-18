package com.appcapital.call_library.aftercall

import AftercallViewPageAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.appcapital.call_library.R
import com.appcapital.call_library.databinding.ActivityAfterCallBinding
import com.appcapital.call_library.utils.SharedPreferencesHelper
import com.appcapital.call_library.utils.Utils
import com.appcapital.call_library.viewmodel.AfterCallViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.ViewModelProvider
import com.appcapital.call_library.service.PhoneCallService

@AndroidEntryPoint
class AfterCallActivity : AppCompatActivity() {
    private val TAG: String = AfterCallActivity::class.simpleName.toString()
    private lateinit var binding: ActivityAfterCallBinding
    private val PERMISSION_REQUEST_READ_CONTACTS = 100
    var phoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAfterCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        phoneNumber = SharedPreferencesHelper.getCalledPhoneNumber(this)
        val appConfig = SharedPreferencesHelper.getAppConfig(this)
        binding.appIcon.setImageResource(appConfig.appIcon)

        val callDuration = intent.getStringExtra("CALL_DURATION")
         binding.callDuration.text = "${getString(R.string.duration)} ${callDuration}"
        binding.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }

        setUpViewPager()
        checkAndRequestContactsPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        val serviceIntent = Intent(this, PhoneCallService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_READ_CONTACTS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, proceed with your logic
                    setCallerDetails()
                }
                return
            }
            else -> {
                // Handle other permissions
            }
        }
    }

    private fun setUpViewPager(){
        val tabLayout = binding.tabLayout
            //afterCallInflatedView.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = binding.viewPager
            //afterCallInflatedView.findViewById<ViewPager2>(R.id.view_pager)
        viewPager.offscreenPageLimit = 3
        val adapter = AftercallViewPageAdapter(this@AfterCallActivity)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.icon  = when (position) {
                0 -> ContextCompat.getDrawable(this, R.drawable.ic_after_call_dash)
                1 -> ContextCompat.getDrawable(this, R.drawable.ic_message)
                2 -> ContextCompat.getDrawable(this, R.drawable.ic_more_option)
                3 -> ContextCompat.getDrawable(this, R.drawable.ic_more_option)
                else -> null
            }
        }.attach()
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
            }
        })
    }

    fun checkAndRequestContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                PERMISSION_REQUEST_READ_CONTACTS
            )
        } else {
            // Permission has already been granted, proceed with your logic
            setCallerDetails()
        }
    }

    private fun setCallerDetails(){
        if(phoneNumber != ""){
            val contactName = Utils.getContactName(this, phoneNumber)
            if(contactName != null){
                binding.phoneNumber.text = contactName
            }else{
                // TODO get user ID from third party
                binding.phoneNumber.text = phoneNumber
            }
        }
    }
}