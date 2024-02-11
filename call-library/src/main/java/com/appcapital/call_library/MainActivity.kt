package com.appcapital.call_library

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.view.Gravity
import androidx.compose.ui.tooling.preview.Preview
import java.util.Timer
import java.util.TimerTask
import android.graphics.Color
import android.widget.Button
import androidx.core.view.marginTop

class MainActivity : ComponentActivity() {
    var isOverlayPermissionScreenOpen:Boolean = false

    private var getContent =
        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.d("RecordingObserver", "onCreate: RESULT OK"+result.resultCode)
            finish()
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("RecordingObserver", "onCreate: RESULT OK")
//              finish()
            }
            // finishAndRemoveTask()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        displayAfterCallScreen(this)
        checkWhenUserAllowsOverlayPermission()
    }


    var handler = Handler()
    var checkOverlaySetting: Runnable = object : Runnable {
        @TargetApi(23)
        override fun run() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return
            }
            if (Settings.canDrawOverlays(this@MainActivity)) {
                //You have the permission, re-launch MainActivity
                val i = Intent(this@MainActivity, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
                return
            }
            handler.postDelayed(this, 1000)
        }
    }


    private fun checkWhenUserAllowsOverlayPermission(){
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (Settings.canDrawOverlays(this@MainActivity) && isOverlayPermissionScreenOpen) {
                    val i = Intent(
                        this@MainActivity,
                        MainActivity::class.java
                    )
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    finish()
                    //   startActivity(i)
                }
            }
        }, 0, 2000) // 5000 milliseconds (5 seconds)
    }

    private fun displayAfterCallScreen(context: Context) {
        Log.d("timerRunner", "saw runner "+Settings.canDrawOverlays(this@MainActivity))

        if (!Settings.canDrawOverlays(this)) {


            addFullScreenGreyView()
        }else{
            showPermissionInstructionOverLay(context)
        }
    }

    private fun addFullScreenGreyView() {
        // Set the alpha value for the translucent effect (0 to 255)
        val alphaValue = 150 // Adjust as needed

        // Create a translucent grey view
        val greyView = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.argb(alphaValue, 0, 0, 0)) // Set grey color with alpha

            // Create a white view
            val whiteView = LinearLayout(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    300
                )
                setBackgroundColor(Color.WHITE)
                orientation = LinearLayout.VERTICAL

                // Add a TextView
                val textView = TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 12 // Set the top margin in pixels (adjust as needed)
                    }
                    text = "You need to allow Overlay permission"
                    gravity = Gravity.CENTER
                    textSize = 18f // Adjust text size as needed
                    // You can customize other properties of the TextView as per your requirement
                }

                // Add a Button
                val continueButton = Button(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 65 // Set the top margin in pixels (adjust as needed)
                    }
                    setBackgroundColor(Color.parseColor("#000000"))
                    setTextColor(Color.parseColor("#FFFFFF"))
                    text = "Continue"
                    // Set OnClickListener for the button
                    setOnClickListener {
                        isOverlayPermissionScreenOpen = true
                        getContent.launch(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                        )
                        handler.postDelayed(checkOverlaySetting, 1000);
                    }
                }
                // Add TextView and Button to the LinearLayout

                setPadding(30.dp, 0, 30.dp, 0)
                addView(textView)
                addView(continueButton)
            }

            addView(whiteView)

            // Adjust the layout parameters of the white view to anchor it to the bottom
            val whiteViewLayoutParams = whiteView.layoutParams as FrameLayout.LayoutParams
            whiteViewLayoutParams.gravity = Gravity.BOTTOM
            whiteView.layoutParams = whiteViewLayoutParams
        }

        // Add the FrameLayout to the activity's content view
        val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(greyView)
    }

    // Function to create a rounded corner drawable
    fun createRoundedCornerDrawable(context: Context, backgroundColor: Int, cornerRadius: Float): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.setColor(backgroundColor)
        drawable.cornerRadius = cornerRadius
        return drawable
    }

    private fun showPermissionInstructionOverLay(context: Context){

        val overlayView = FrameLayout(context).apply {
            val paddingValue = 70.dp
            setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        }
        val backgroundColor = Color.argb(150, 0, 0, 0)
        val contentViewColor = Color.WHITE
        overlayView.setBackgroundColor(backgroundColor)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.BOTTOM
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

//    val textView = TextView(context).apply {
//        text = "Allow access to manage all files"
//        this.setTextColor(Color.WHITE)
//        textSize = 16f
//    }

        val textLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1f
        }

        //textView.layoutParams = textLayoutParams

        // val toggleButton = Switch(context).apply {}
        // toggleButton.isEnabled = false
        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
            // addView(textView)
            //addView(toggleButton)
            background = createRoundedCornerDrawable(context, contentViewColor, 40f)
            val paddingValue = 20.dp
            setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        }

        linearLayout.layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        overlayView.addView(linearLayout)
        windowManager.addView(overlayView, layoutParams)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                // windowManager.removeView(overlayView)
            },
            4000 // value in milliseconds
        )
        finish()
    }

}
val Int.dp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()