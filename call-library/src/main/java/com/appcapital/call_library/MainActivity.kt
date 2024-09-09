package com.appcapital.call_library

import AftercallViewPageAdapter
import androidx.core.content.ContextCompat
import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.appcapital.call_library.utils.SharedPreferencesHelper
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import java.util.Timer
import java.util.TimerTask
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {
    var isOverlayPermissionScreenOpen: Boolean = false
    private lateinit var greyView: View
    private lateinit var linearLayout: LinearLayout
    private lateinit var gestureDetector: GestureDetector
    private lateinit var overlayView: FrameLayout
    private lateinit var afterCallInflatedView: View
    val outMetrics = DisplayMetrics()
    lateinit var adSize: AdSize
    private var getContent =
        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.d("RecordingObserver", "onCreate: RESULT OK" + result.resultCode)
            finish()
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("RecordingObserver", "onCreate: RESULT OK")
//              finish()
            }
            // finishAndRemoveTask()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        afterCallInflatedView = inflateXmlLayout(this, R.layout.after_call_display)
        setUpViewPager()
        MobileAds.initialize(this) {}
        requestPermissions()
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                dismissOverlay()
                return true
            }
        })
        val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(outMetrics)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                windowManager.removeView(overlayView)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }
    private fun setUpViewPager(){
        val tabLayout = afterCallInflatedView.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = afterCallInflatedView.findViewById<ViewPager2>(R.id.view_pager)
        viewPager.offscreenPageLimit = 3
        val adapter = AftercallViewPageAdapter(this@MainActivity)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.icon  = when (position) {
                0 -> ContextCompat.getDrawable(this, android.R.drawable.ic_menu_compass)
                1 -> ContextCompat.getDrawable(this, android.R.drawable.ic_menu_compass)
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
    private fun getAdSize(context: Context,adWidthPixels: Float): AdSize{
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density

        val screenWidth = outMetrics.widthPixels
        val adWidth = ((screenWidth / density).toInt() - 40)
        Log.d("ADD_STAT_WIDTH","$screenWidth --- ${density}")
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }
    private fun requestPermissions(){
        if (applicationContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                1000
            )
        }

        if (applicationContext.checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS),
                2000
            )
        }
    }

    override fun onResume() {
        super.onResume()
        displayAfterCallScreen(this)
        checkWhenUserAllowsOverlayPermission()
    }

    private fun dismissOverlay() {
        // Remove the overlay from the rootView
        windowManager.removeView(overlayView)
    }

    private fun checkWhenUserAllowsOverlayPermission() {
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
        Log.d("timerRunner", "saw runner " + Settings.canDrawOverlays(this@MainActivity))

        if (!Settings.canDrawOverlays(this)) {
            permissionRequestView()
        } else {
            showAfterCallOverLay(context)
        }
    }

    private fun permissionRequestView() {
        // Set the alpha value for the translucent effect (0 to 255)
        val alphaValue = 150 // Adjust as needed

        // Create a translucent grey view
        greyView = FrameLayout(this).apply {
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
                    setTextColor(Color.BLACK)
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


    private fun createLinearLayout(context: Context): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            // addView(textView)
            //addView(toggleButton)
            val appConfig = SharedPreferencesHelper.getAppConfig(context)

            val closeBtn: ImageView? = afterCallInflatedView.findViewById(R.id.close_btn)
            val goToAppTextView: TextView? = afterCallInflatedView.findViewById(R.id.go_to_app_text)
            val appIconImageView: ImageView? = afterCallInflatedView.findViewById(R.id.app_icon)

            val appInfoView: LinearLayout? = afterCallInflatedView.findViewById(R.id.continue_to_app_lin)
            val adOneView: LinearLayout = afterCallInflatedView.findViewById(R.id.ad_one)
            val adTwoView: LinearLayout = afterCallInflatedView.findViewById(R.id.ad_two)

// Define the desired height in pixels (you can change this value as needed)
//            val screenHeight = DisplayMetrics().heightPixels
//            val adTwoHeight = screenHeight / 4

            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()

// Get display metrics of the screen
            windowManager.defaultDisplay.getMetrics(displayMetrics)

// Calculate the height (one-fourth of screen height)
            val screenHeight = displayMetrics.heightPixels
            val quarterScreenHeight = screenHeight / 3

            Log.d("adTwoHeight","is $quarterScreenHeight")
            val existingLayoutParams = adTwoView.layoutParams
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                quarterScreenHeight
            )
            layoutParams.setMargins(0, 8, 0, 12)

//            layoutParams.height = desiredHeightInPixels
            adTwoView.layoutParams = layoutParams
//
//            layoutParams.height = layoutParams
            val adsParentContainer = afterCallInflatedView.findViewById<LinearLayout>(R.id.ad_lin)
            var adWidthPixels = adsParentContainer.width.toFloat()
          //  if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
           // }
           adSize = getAdSize(this@MainActivity,adWidthPixels)
            appInfoView?.setBackgroundColor(appConfig.primaryColor)
            appIconImageView?.setImageResource(appConfig.appIcon)
            loadBanner(adOneView)
            loadNativeAd(adTwoView)
            goToAppTextView?.text = appConfig.appName
            appInfoView?.setOnClickListener {
                openApp(
                    appConfig.packageName,
                    appConfig.classEntryName
                )
            }
            closeBtn?.setOnClickListener {
                finish()
                windowManager.removeView(overlayView)
            }
            addView(afterCallInflatedView)
            background = createRoundedCornerDrawable(context, Color.WHITE, 40f)
            val paddingValue = 20.dp
            setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        }
    }

    private fun openApp(packageName: String, classEntryName: String) {
           finish()
           windowManager.removeView(overlayView)
           Log.d("ENTRYCLASSNAME","$classEntryName")
//           val i = Intent()
//           i.setAction(Intent.ACTION_VIEW)
//           i.setClassName(
//               packageName,
//               "screen.recorder.MainActivity"
//           )
//           startActivity(i)
        val intent = Intent(Intent.ACTION_MAIN)
        intent.setComponent(
            ComponentName(
                packageName,
                classEntryName
            )
        )
//        startActivity(intent)
//        var intent = packageManager.getLaunchIntentForPackage(classEntryName)
//        if (intent != null) {
//            // We found the activity now start the activity
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        } else {
//            // Bring user to the market or let them choose an app?
//            intent = Intent(Intent.ACTION_VIEW)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.setData(Uri.parse("market://details?id=" + "com.package.name"))
//            startActivity(intent)
//        }
    }

    // Function to create a rounded corner drawable
    fun createRoundedCornerDrawable(
        context: Context,
        backgroundColor: Int,
        cornerRadius: Float
    ): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.setColor(backgroundColor)
        drawable.cornerRadius = cornerRadius
        return drawable
    }

    override fun onBackPressed() {
        Log.d("BACKPRESSED", "yes")
        finish()
        windowManager.removeView(overlayView)
    }

    private fun loadBanner(adViewHolder: LinearLayout) {

        // Create a new ad view.
        val adView = AdView(this)
        adView.setAdSize(adSize)
        adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)

        adView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.d("ADD_STAT","FAILED $adError")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.d("ADD_STAT","CLICKED")
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("ADD_STAT","LOADED")
                adViewHolder.addView(adView)
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d("ADD_STAT","OPENED")
            }
        }
    }
    private fun loadNativeAd(adViewHolder: LinearLayout) {
      //  val adContainer: FrameLayout = itemView.findViewById(R.id.ad_frame_container)
        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad : NativeAd ->
                // Show the ad.
                Log.d("nativeAdSuccess","True")
                val adView = layoutInflater
                    .inflate(R.layout.ad_layout, null) as NativeAdView
                populateAd(ad, adView, adViewHolder)
                //adViewHolder.addView(ad.)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                    Log.d("nativeAdFail","${adError.responseInfo}")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder()
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build())
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateAd(ad: NativeAd, adView: NativeAdView, adViewHolder: LinearLayout){
        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        headlineView.text = ad.headline
        adView.headlineView = headlineView
        adViewHolder.addView(adView)
    }

    fun inflateXmlLayout(context: Context, layoutResId: Int): View {
        return LayoutInflater.from(context).inflate(layoutResId, null)
    }

    private fun showAfterCallOverLay(context: Context) {

        overlayView = FrameLayout(context).apply {
            val paddingValue = 70.dp
            setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        }
        val backgroundColor = Color.argb(150, 0, 0, 0)
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
        linearLayout = createLinearLayout(this)
        linearLayout.setOnTouchListener { _, event ->
//            Log.d("REMOVEOVERLAY","remove over lay");
//            windowManager.removeView(overlayView)
            true
        }

        linearLayout.layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        overlayView.addView(linearLayout)
        windowManager.addView(overlayView, layoutParams)

        Handler(Looper.getMainLooper()).postDelayed(
            {
//               windowManager.removeView(overlayView)
            },
            4000 // value in milliseconds
        )
        finish()
    }

}

val Int.dp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()