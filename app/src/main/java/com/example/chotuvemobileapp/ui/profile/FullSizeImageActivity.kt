package com.example.chotuvemobileapp.ui.profile

import android.animation.ObjectAnimator
import android.os.Bundle
import android.transition.Transition
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

        window.sharedElementEnterTransition.addListener(object: Transition.TransitionListener{
            override fun onTransitionEnd(transition: Transition?) {}
            override fun onTransitionResume(transition: Transition?) {}
            override fun onTransitionPause(transition: Transition?) {}
            override fun onTransitionCancel(transition: Transition?) {}
            override fun onTransitionStart(transition: Transition?) {
                val animator = ObjectAnimator.ofFloat(FullImageWrapper, "radius", 0f)
                animator.duration = 300
                animator.start()
            }

        })
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

    override fun onBackPressed() {
        super.onBackPressed()
        val animator = ObjectAnimator.ofFloat(FullImageWrapper, "radius", resources.getDimension(R.dimen.img_radius))
        animator.duration = 200
        animator.start()
    }
}