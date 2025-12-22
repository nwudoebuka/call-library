package com.appcapital.call_library.newdesign.prominentdisclosure

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.appcapital.call_library.R
import com.appcapital.call_library.databinding.ActivityProminentDisclosureBinding
import com.appcapital.call_library.newdesign.AfterCallWindow
import com.appcapital.call_library.newdesign.NewAfterCallActivity

class ProminentDisclosureActivity : AppCompatActivity() {

    private val binding: ActivityProminentDisclosureBinding by lazy {
        ActivityProminentDisclosureBinding.inflate(layoutInflater)
    }

    val REQUEST_CODE = 2001

    //private lateinit var permissionFlowManager: PermissionFlowManager

    private val permissions = permissionQueue.toMutableList()
    private var current: PermissionConfig? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.lifecycleOwner = this

        start()

        binding.btnAllowaccess.setOnClickListener {
            onAllow()
        }

        binding.btnMaybeLater.setOnClickListener {
            onSkip()
        }
    }


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
                this,
                current!!.permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showNext()
        } else {
            current?.let {
                show(it)
            }

        }
    }


    fun show(config: PermissionConfig) {
        binding.locationIcon.setImageResource(config.iconRes)
        binding.permissionTitle.text = config.title
        binding.locationPermissionReason.text = config.description
    }

    fun onSkip() {
        showNext()
    }

    fun onAllow() {
        requestCurrentPermission()
    }

    fun onComplete() {
        val intent: Intent = Intent(this, NewAfterCallActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun requestCurrentPermission() {
        current?.let {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(it.permission),
                REQUEST_CODE
            )
        }
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            onPermissionResult()
        }
    }

    fun onPermissionResult() {
        showNext()
    }


    fun initOnClickListener() {
        binding.btnAllowaccess.setOnClickListener {
            val intent: Intent = Intent(this, NewAfterCallActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }
}