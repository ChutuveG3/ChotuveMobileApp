package com.example.chotuvemobileapp

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.ui.CommentsFragment
import com.example.chotuvemobileapp.ui.EmptyListFragment
import kotlinx.android.synthetic.main.activity_play_video.*


class PlayVideoActivity : AppCompatActivity() {

    private var nComments = 0
    private var fullscreen = false
    private var backgroundColor = 0
    private val delayTime: Long = 5000
    private val displayMetrics = DisplayMetrics()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        windowManager.defaultDisplay.getMetrics(displayMetrics)
        VideoWrapper.maxHeight = displayMetrics.heightPixels / 2

        VideoTitle.text = intent.getStringExtra("videoTitle")
        VideoAuthor.text = intent.getStringExtra("videoAuthor")
        VideoProgressBar.visibility = View.VISIBLE

        backgroundColor = getColor(R.color.white)
        val description = (intent.getStringExtra("videoDate")!!).plus("\n")
            .plus(intent.getStringExtra("description"))

        VideoDescription.text = description

        nComments = intent.getIntExtra("comments", 0)

        if (nComments == 0) supportFragmentManager.beginTransaction().replace(R.id.VideoCommentsFragment, EmptyListFragment()).commit()
        else supportFragmentManager.beginTransaction().replace(R.id.VideoCommentsFragment, CommentsFragment.newInstance(nComments)).commit()

        val uri = Uri.parse(intent.getStringExtra("url"))

        val mediaController = MediaController(this)
        mediaController.setAnchorView(VideoWrapper)
        mediaController.setMediaPlayer(Video)

        Video.setMediaController(mediaController)
        Video.setVideoURI(uri)
        Video.setOnPreparedListener{
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT)
            VideoProgressBar.visibility = View.GONE
            Video.start()
        }
        Video.setOnInfoListener { _, what, _ ->
            when (what) {
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                    VideoProgressBar.visibility = View.GONE
                    true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    VideoProgressBar.visibility = View.VISIBLE
                    true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    VideoProgressBar.visibility = View.GONE
                    true
                }
                else -> false
            }
        }

        Video.setOnClickListener {
            FullscreenToggle.visibility = View.VISIBLE
            Handler().postDelayed({FullscreenToggle.visibility = View.GONE}, delayTime)
            Handler().postDelayed({ if (fullscreen) setFullscreenFlags() }, delayTime)
        }

        FullscreenToggle.setOnClickListener {
            if (!fullscreen) makeFullScreen()
            else exitFullScreen()
        }
        Handler().postDelayed({FullscreenToggle.visibility = View.GONE}, delayTime)

    }

    private fun setLayout(height: Int){
        Video.layoutParams.height = height
        VideoWrapper.layoutParams.height = height
        Video.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        VideoWrapper.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    }

    private fun setFullscreenFlags(){
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun makeFullScreen(){
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.setBackgroundColor(getColor(R.color.black))
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        setFullscreenFlags()
        if(Video.width > Video.height) this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else VideoWrapper.maxHeight = displayMetrics.heightPixels
        setLayout(ViewGroup.LayoutParams.MATCH_PARENT)
        fullscreen = true
    }

    private fun exitFullScreen(){
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        window.decorView.setBackgroundColor(backgroundColor)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        setLayout(ViewGroup.LayoutParams.WRAP_CONTENT)
        if(Video.height > Video.width) VideoWrapper.maxHeight = displayMetrics.heightPixels / 2
        fullscreen = false
    }

    override fun onBackPressed() {
        if (fullscreen) exitFullScreen()
        else super.onBackPressed()
    }
}
