package com.appcapital.call_library.newdesign

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.appcapital.call_library.R
import com.appcapital.call_library.databinding.ActivityNewAfterCallBinding

class NewAfterCallActivity : AppCompatActivity() {


    val binding: ActivityNewAfterCallBinding by lazy {
        ActivityNewAfterCallBinding.inflate(layoutInflater)
    }
    val viewModel: NewAfterCallViewModel by lazy {
        ViewModelProvider(this)[ NewAfterCallViewModel::class.java]
    }

    val afterCallWindow = AfterCallWindow(this@NewAfterCallActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(binding.root)
        afterCallWindow.show()

    }

    override fun onPause() {
        super.onPause()
        afterCallWindow.remove()
    }

    override fun onResume() {
        super.onResume()
        afterCallWindow.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}