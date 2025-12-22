package com.appcapital.call_library.newdesign

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.appcapital.call_library.R
import com.appcapital.call_library.databinding.ActivityNewAfterCallBinding
import com.appcapital.call_library.utils.SharedPreferencesHelper
import com.appcapital.call_library.utils.Utils
import com.appcapital.call_library.utils.Utils.Companion.getContactPhotoUri
import com.appcapital.call_library.utils.Utils.Companion.getLastCallDuration

class AfterCallWindow(private val context: Context) {

    private var windowManager: WindowManager? = null
    private var binding: ActivityNewAfterCallBinding? = null

    private var phoneNumber : String = "08035764450"
    var selectedQuickMessage = ""


    fun show() {
        if (binding != null) return

        // Overlay permission check should be done BEFORE calling show()

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        phoneNumber  = Utils.getContactNumber(context).toString()

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.activity_new_after_call,
            null,
            false
        )

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        windowManager?.addView(binding!!.root, params)

        setupUi()
    }


    private fun setupUi() = with(binding!!) {

        submitButton.setOnClickListener {
            remove()
        }
        val durationSeconds = getLastCallDuration(context)

        val lastNumber = Utils.getLastPhoneNumber(context).toString()

        // Example: update values via binding
        headingGender.text = "Call Ended"
        userName.text = Utils.getContactName(context,Utils.getLastPhoneNumber(context).toString())
        callDuration.text = Utils.formatDuration(durationSeconds)
        date.text = Utils.getFormattedDateTime(Utils.getLastCallDate(context)?.toLong()?:0L)


        val photoUri = lastNumber?.let {
            getContactPhotoUri(context, it)
        }

        if (photoUri != null) {
            binding!!.profileImage.setImageURI(photoUri)
        } else {
            binding!!.profileImage.setImageResource(R.drawable.ic_profile_new)
        }
        dragHandle.isClickable = true

        // Swipe down to dismiss
        dragHandle.setOnTouchListener(object : View.OnTouchListener {

            private var startY = 0f

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                v?.performClick()
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> startY = event.rawY
                    MotionEvent.ACTION_UP -> {
                        if (event.rawY - startY > 150) {
                            remove()
                        }
                    }
                }
                return true
            }
        })


        //quick message
        val quickMessageOptions = listOf(context.getString(R.string.I_am_busy), context.getString(R.string.let_me_call_back), context.getString(R.string.in_a_meeting), context.getString(R.string.cant_talk))
        binding!!.addNote.setOnClickListener {
                val phoneNumber = SharedPreferencesHelper.getCalledPhoneNumber(context)
                val message = selectedQuickMessage

                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:$phoneNumber")
                    putExtra("sms_body", message)
                }
                context.startActivity(smsIntent)
            }


    }



    fun remove() {
        try {
            binding?.let {
                windowManager?.removeView(it.root)
                binding = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
