package com.example.chotuvemobileapp

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        VideoTitle.text = intent.getStringExtra("videoTitle")
        VideoAuthor.text = intent.getStringExtra("videoAuthor")
        VideoProgressBar.visibility = View.VISIBLE

        backgroundColor = getColor(R.color.white)
        val description = (intent.getStringExtra("videoDate")!!).plus("\nEsto es una descripción recontra hadcodeada para ver que onda esto de reproducir un video\nViva Perón!!")

        VideoDescription.text = description

        nComments = intent.getIntExtra("comments", 0)

        if (nComments == 0) supportFragmentManager.beginTransaction().replace(R.id.VideoCommentsFragment, EmptyListFragment()).commit()
        else supportFragmentManager.beginTransaction().replace(R.id.VideoCommentsFragment, CommentsFragment.newInstance(nComments)).commit()

        val uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/lol-videos-8dc74.appspot.com/o/Blog_Images%2Fvideo%3A10142?alt=media&token=9f7734fa-f714-4838-bd65-8a4d594ec2ce")

        val mediaController = MediaController(this)
        mediaController.setAnchorView(VideoWrapper)
        mediaController.setMediaPlayer(Video)

        Video.setMediaController(mediaController)
        Video.setVideoURI(uri)
        Video.setOnPreparedListener{
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
            Handler().postDelayed({
                if (fullscreen){
                window.decorView.apply {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                }}
            }, delayTime)
        }

        FullscreenToggle.setOnClickListener {
            if (!fullscreen) {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.decorView.apply {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                }
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                window.decorView.setBackgroundColor(getColor(R.color.black))
                Video.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                Video.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                fullscreen = true
            }
            else{
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
                window.decorView.setBackgroundColor(backgroundColor)
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                Video.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                Video.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                fullscreen = false
            }
        }
        Handler().postDelayed({FullscreenToggle.visibility = View.GONE}, delayTime)

    }
}
