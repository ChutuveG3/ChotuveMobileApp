package com.example.chotuvemobileapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chotuvemobileapp.data.repositories.ReactionsDataSource
import com.example.chotuvemobileapp.data.requests.reactions.CommentRequest
import com.example.chotuvemobileapp.entities.CommentItem
import com.example.chotuvemobileapp.helpers.CommentsAdapter
import com.example.chotuvemobileapp.helpers.Utilities.REACTION_DISLIKE
import com.example.chotuvemobileapp.helpers.Utilities.REACTION_LIKE
import com.example.chotuvemobileapp.helpers.Utilities.SUCCESS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.helpers.Utilities.nowDateTimeStr
import com.example.chotuvemobileapp.helpers.VideoReaction
import com.example.chotuvemobileapp.viewmodels.PlayVideoViewModel
import kotlinx.android.synthetic.main.activity_play_video.*


@Suppress("UNUSED_PARAMETER")
class PlayVideoActivity : AppCompatActivity() {

    private var fullscreen = false
    private var backgroundColor = 0
    private val delayTime: Long = 5000
    private val displayMetrics = DisplayMetrics()
    private val videoId by lazy { intent.getStringExtra("videoId") }
    private val uri by lazy { Uri.parse(intent.getStringExtra("url")) }
    private val mediaController by lazy { MediaController(this) }
    private val prefs by lazy {
        getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val viewModel by lazy {
        ViewModelProvider(this).get(PlayVideoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        viewModel.setValues(prefs, videoId)

        windowManager.defaultDisplay.getMetrics(displayMetrics)
        VideoWrapper.maxHeight = displayMetrics.heightPixels / 2

        VideoTitle.text = intent.getStringExtra("videoTitle")
        VideoAuthor.text = intent.getStringExtra("videoAuthor")

        NoCommentsImageView.visibility = View.GONE
        NoCommentsTextView.visibility = View.GONE
        PostCommentButton.visibility = View.GONE

        VideoProgressBar.visibility = View.VISIBLE

        backgroundColor = getColor(R.color.white)

        viewModel.video.observe(this, Observer {
            val description = intent.getStringExtra("videoDate")!! + "\n" + it.description
            VideoDescription.text = description
            LikeCount.text = it.likes.toString()
            DislikeCount.text = it.dislikes.toString()
            val (colorLike, colorDislike) = mapColors(it.reaction)
            ImageViewCompat.setImageTintList(
                LikeButton,
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, colorLike))
            )
            ImageViewCompat.setImageTintList(
                DislikeButton,
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, colorDislike))
            )
        })
        viewModel.comments.observe(this, Observer {
            if (it.isEmpty()) {
                CommentsRecyclerView.visibility = View.GONE
                NoCommentsImageView.visibility = View.VISIBLE
                NoCommentsTextView.visibility = View.VISIBLE
            }
            else{
                CommentsRecyclerView.adapter = CommentsAdapter(it)
                CommentsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
            }
        })

        AddCommentInput.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                PostCommentButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

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

        Handler().postDelayed({FullscreenToggle.visibility = View.GONE}, delayTime)
    }

    fun toggleFullScreen(view: View) {
        if (!fullscreen) makeFullScreen()
        else exitFullScreen()
    }

    fun tapVideo(view: View) {
        FullscreenToggle.visibility = View.VISIBLE
        Handler().postDelayed({ FullscreenToggle.visibility = View.GONE }, delayTime)
        Handler().postDelayed({ if (fullscreen) setFullscreenFlags() }, delayTime)
    }

    fun handleDescriptionToggle(view: View) {
        if (viewModel.descriptionExpanded) {
            viewModel.descriptionExpanded = false
            toggleDescription(0, 0, 0F)
        } else {
            viewModel.descriptionExpanded = true
            toggleDescription(8, ViewGroup.LayoutParams.WRAP_CONTENT, 180F)
        }
    }

    fun dislike(view: View) {
        view as ImageView
        if (viewModel.video.value?.reaction == REACTION_DISLIKE){
            viewModel.video.value!!.reaction = null
            setButtonColor(R.color.white, view)
            viewModel.video.value!!.dislikes -= 1
            ReactionsDataSource.reactToVideo(prefs, videoId, VideoReaction.Undislike){}
            }
        else{
            viewModel.video.value!!.reaction = REACTION_DISLIKE
            if (viewModel.video.value?.reaction == REACTION_LIKE) {
                setButtonColor(R.color.white, LikeButton)
                viewModel.video.value!!.likes -= 1
            }
            setButtonColor(R.color.dislike, view)
            viewModel.video.value!!.dislikes += 1
            ReactionsDataSource.reactToVideo(prefs, videoId, VideoReaction.Dislike){}
        }
    }

    fun like(view: View) {
        view as ImageView
        if (viewModel.video.value?.reaction == REACTION_LIKE) {
            viewModel.video.value!!.reaction = null
            setButtonColor(R.color.white, view)
            viewModel.video.value!!.likes -= 1
            ReactionsDataSource.reactToVideo(prefs, videoId, VideoReaction.Unlike){}
        } else {
            viewModel.video.value!!.reaction = REACTION_LIKE
            if (viewModel.video.value?.reaction == REACTION_DISLIKE) {
                setButtonColor(R.color.white, DislikeButton)
                viewModel.video.value!!.dislikes -= 1
            }
            setButtonColor(R.color.like, view)
            viewModel.video.value!!.likes += 1
            ReactionsDataSource.reactToVideo(prefs, videoId, VideoReaction.Like){}
        }
    }

    fun startProfileActivity(view: View) {
        val profileIntent = Intent(this, UserProfileActivity::class.java)
        profileIntent.putExtra(USERNAME, intent.getStringExtra("videoAuthor"))
        startActivity(profileIntent)
    }

    fun postComment(view: View) {
        val comment = CommentRequest(nowDateTimeStr(), AddCommentInput.text.toString())
        ReactionsDataSource.commentVideo(prefs, videoId, comment){
            val text: String
            when(it){
                SUCCESS_MESSAGE -> {
                    text = getString(R.string.comment_post_success)
                    viewModel.addComment(CommentItem(comment.comment, prefs.getString(USERNAME, "")!!, comment.datetime))
                }
                else -> text = getString(R.string.internal_error)
            }
            Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
        }
    }

    private fun toggleDescription(margin: Int, height: Int, rotation: Float) {
        val constraints = ConstraintSet()
        constraints.clone(ScrollViewLayout)
        constraints.connect(R.id.VideoDescription, ConstraintSet.TOP, R.id.VideoDescriptionHeader, ConstraintSet.BOTTOM, margin)
        constraints.connect(R.id.StickyPart, ConstraintSet.TOP, R.id.VideoDescription, ConstraintSet.BOTTOM, margin)
        constraints.applyTo(ScrollViewLayout)
        VideoDescription.layoutParams.height = height
        DescriptionToggle.rotationX = rotation
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

    private fun setButtonColor(color: Int, button: ImageView){
        ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(ContextCompat.getColor(applicationContext, color)))
    }

    data class ColorResult(val colorLike: Int, val colorDislike: Int)
    private fun mapColors(reaction: String?): ColorResult{
        return when(reaction){
            "like" -> ColorResult(R.color.like, R.color.white)
            "dislike" -> ColorResult(R.color.white, R.color.dislike)
            else -> ColorResult(R.color.white, R.color.white)
        }
    }
}
