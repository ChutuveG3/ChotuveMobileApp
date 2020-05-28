package com.example.chotuvemobileapp

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.ui.CommentsFragment
import com.example.chotuvemobileapp.ui.EmptyListFragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_play_video.*


class PlayVideoActivity : AppCompatActivity() {

    private lateinit var mStorageRef : StorageReference
    private var nComments = 0
    private var fullscreen = false
    private val metrics = DisplayMetrics()
    private lateinit var originalParams: ViewGroup.LayoutParams

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        VideoTitle.text = intent.getStringExtra("videoTitle")
        VideoAuthor.text = intent.getStringExtra("videoAuthor")
        VideoProgressBar.visibility = View.VISIBLE

        val description = (intent.getStringExtra("videoDate")!!).plus("\nEsto es una descripción recontra hadcodeada para ver que onda esto de reproducir un video\nViva Perón!!")

        VideoDescription.text = description

        nComments = intent.getIntExtra("comments", 0)

        if (nComments == 0) supportFragmentManager.beginTransaction().replace(R.id.VideoCommentsFragment, EmptyListFragment()).commit()
        else supportFragmentManager.beginTransaction().replace(R.id.VideoCommentsFragment, CommentsFragment.newInstance(nComments)).commit()

        mStorageRef = FirebaseStorage.getInstance().reference

        val uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/chotuve-g3.appspot.com/o/VID-20200430-WA0008.mp4?alt=media&token=3777c88c-2116-490b-a652-12d1f84106ed")

        val mediaController = MediaController(this)
        mediaController.setAnchorView(VideoWrapper)
        mediaController.setMediaPlayer(Video)

        Video.setMediaController(mediaController)
        Video.setVideoURI(uri)
        Video.setOnPreparedListener{
            originalParams = Video.layoutParams
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

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        FullscreenToggle.setOnClickListener {
            if (!fullscreen) {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val newParams = Video.layoutParams
                newParams.height = metrics.heightPixels
                newParams.width = metrics.widthPixels
                Video.layoutParams = newParams
                fullscreen = true
            }
            else{
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                Video.layoutParams = originalParams
                fullscreen = false
            }
        }

    }
}
