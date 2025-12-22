package com.appcapital.call_library.newdesign.prominentdisclosure

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.appcapital.call_library.R
import com.appcapital.call_library.databinding.ActivityProminentDisclosureBinding

class PermissionOverlayWindow(
    private val context: Context,
    private val onAllow: () -> Unit,
    private val onSkip: () -> Unit
) {

    private var windowManager: WindowManager? = null
    private var binding: ActivityProminentDisclosureBinding? = null

    fun show(config: PermissionConfig) {
        if (binding != null) return

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.activity_prominent_disclosure,
            null,
            false
        )

        binding!!.locationIcon .setImageResource(config.iconRes)
        binding!!.permissionTitle.text = config.title
        binding!!.locationPermissionReason.text = config.description

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            type,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        windowManager?.addView(binding!!.root, params)

        binding!!.btnAllowaccess.setOnClickListener {
            remove()
            onAllow()
        }

        binding!!.btnMaybeLater.setOnClickListener {
            onSkip()
            remove()
        }
    }


    fun remove() {
        try {
            binding?.let {
                windowManager?.removeView(it.root)
                binding = null
            }
        } catch (_: Exception) {}
    }
}
