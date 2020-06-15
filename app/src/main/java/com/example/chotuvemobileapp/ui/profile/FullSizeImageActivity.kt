package com.example.chotuvemobileapp.ui.profile

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import com.bumptech.glide.Glide
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.helpers.Utilities
import kotlinx.android.synthetic.main.fragment_full_size_image.*

class FullSizeImageActivity : AppCompatActivity() {

    private val url by lazy{
        intent.getStringExtra(Utilities.PIC_URL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_full_size_image)

        if (url != null) {
            Glide.with(this).load(url).into(FullScreenImage)
        }
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.setBackgroundColor(getColor(applicationContext, R.color.black))
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    }

    override fun onDestroy() {
        super.onDestroy()
        window.decorView.setBackgroundColor(getColor(applicationContext, R.color.white))
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}