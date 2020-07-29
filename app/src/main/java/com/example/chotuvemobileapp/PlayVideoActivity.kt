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
import android.text.*
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.italic
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chotuvemobileapp.data.repositories.ProfileInfoDataSource
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

        PostCommentButton.visibility = View.GONE

        VideoProgressBar.visibility = View.VISIBLE
        showLoadingVideoInfoScreen()

        backgroundColor = getColor(R.color.white)

        viewModel.video.observe(this, Observer {
            ProfileInfoDataSource.getProfileInfo(prefs, intent.getStringExtra("videoAuthor")!!){profile ->
                if (profile?.profile_img_url != null)
                    Glide
                        .with(this)
                        .load(Uri.parse(profile.profile_img_url))
                        .centerCrop()
                        .into(VideoAuthorPic)
            }
            val description = SpannableStringBuilder()
                .bold { append("${it.views} views\n") }
                .italic { append(intent.getStringExtra("videoDate")!!) }
                .append("\n\n${if (it.description.isBlank()) getString(R.string.no_description) else it.description}")

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
            quitLoadingVideoInfo()
        })
        viewModel.comments.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                NoCommentsImageView.visibility = View.VISIBLE
                NoCommentsTextView.visibility = View.VISIBLE
            }
            else{
                CommentsRecyclerView.adapter = CommentsAdapter(it)
                CommentsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                NoCommentsImageView.visibility = View.GONE
                NoCommentsTextView.visibility = View.GONE
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
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, false)
            VideoProgressBar.visibility = View.GONE
            Video.start()
        }
        Video.setOnInfoListener { _, what, _ ->
            when (what) {
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START, MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    VideoProgressBar.visibility = View.GONE
                    true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    VideoProgressBar.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }

        Handler().postDelayed({FullscreenToggle.visibility = View.GONE}, delayTime)
    }

    private fun quitLoadingVideoInfo() {
        VideoInfoProgressBar.visibility = View.GONE
        ScrollViewBlocker.visibility = View.GONE
        ScrollViewLayout.alpha = 1F
    }

    private fun showLoadingVideoInfoScreen() {
        VideoInfoProgressBar.visibility = View.VISIBLE
        ScrollViewLayout.alpha = .2F
        ScrollViewBlocker.visibility = View.VISIBLE
    }

    fun toggleFullScreen(view: View) = if (!fullscreen) makeFullScreen() else exitFullScreen()

    fun tapVideo(view: View) {
        FullscreenToggle.visibility = View.VISIBLE
        Handler().postDelayed({ FullscreenToggle.visibility = View.GONE }, delayTime)
        Handler().postDelayed({ if (fullscreen) setFullscreenFlags() }, delayTime)
    }

    fun handleDescriptionToggle(view: View) {
        if (viewModel.descriptionExpanded) {
            viewModel.descriptionExpanded = false
            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_back))
            toggleDescription(0, 0)
        } else {
            viewModel.descriptionExpanded = true
            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip))
            toggleDescription(8, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    fun dislike(view: View) {
        view as ImageView
        if (viewModel.video.value?.reaction == REACTION_DISLIKE){
            ReactionsDataSource.reactToVideo(prefs, videoId, VideoReaction.Undislike) {
                if (it == SUCCESS_MESSAGE) {
                    setButtonColor(R.color.white, view)
                    viewModel.react(VideoReaction.Undislike)
                }
            }
        }
        else{
            ReactionsDataSource.reactToVideo(prefs, videoId, VideoReaction.Dislike) {
                if (it == SUCCESS_MESSAGE) {
                    if (viewModel.video.value?.reaction == REACTION_LIKE) setButtonColor(R.color.white, LikeButton)

                    setButtonColor(R.color.dislike, view)
                    viewModel.react(VideoReaction.Dislike)
                    Toast.makeText(applicationContext, getString(R.string.toast_dislike), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun like(view: View) {
        view as ImageView
        if (viewModel.video.value?.reaction == REACTION_LIKE) {
            ReactionsDataSource.reactToVideo(prefs, videoId, VideoReaction.Unlike) {
                if (it == SUCCESS_MESSAGE) {
                    setButtonColor(R.color.white, view)
                    viewModel.react(VideoReaction.Unlike)
                }
            }
        }
        else {
            ReactionsDataSource.reactToVideo(prefs, videoId, VideoReaction.Like) {
                if (it == SUCCESS_MESSAGE) {
                    if (viewModel.video.value?.reaction == REACTION_DISLIKE) setButtonColor(R.color.white, DislikeButton)

                    setButtonColor(R.color.like, view)
                    viewModel.react(VideoReaction.Like)
                    Toast.makeText(applicationContext, getString(R.string.toast_like), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun startProfileActivity(view: View) {
        val profileIntent = Intent(this, UserProfileActivity::class.java)
        profileIntent.putExtra(USERNAME, intent.getStringExtra("videoAuthor"))
        startActivity(profileIntent)
    }

    fun postComment(view: View) {
        showLoadingVideoInfoScreen()
        val comment = CommentRequest(nowDateTimeStr(), AddCommentInput.text.toString())
        ReactionsDataSource.commentVideo(prefs, videoId, comment){
            val text: String
            when(it){
                SUCCESS_MESSAGE -> {
                    AddCommentInput.text.clear()
                    AddCommentInput.clearFocus()
                    StickyPart.requestFocus()
                    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    text = getString(R.string.comment_post_success)
                    viewModel.addComment(CommentItem(comment.comment, prefs.getString(USERNAME, "")!!, comment.datetime))
                }
                else -> text = getString(R.string.internal_error)
            }
            quitLoadingVideoInfo()
            Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
        }
    }

    private fun toggleDescription(margin: Int, height: Int) {
        val constraints = ConstraintSet()
        constraints.clone(ScrollViewLayout)
        constraints.connect(R.id.VideoDescription, ConstraintSet.TOP, R.id.VideoDescriptionHeader, ConstraintSet.BOTTOM, margin)
        constraints.connect(R.id.StickyPart, ConstraintSet.TOP, R.id.VideoDescription, ConstraintSet.BOTTOM, margin)
        constraints.applyTo(ScrollViewLayout)
        VideoDescription.layoutParams.height = height
    }

    private fun setLayout(height: Int, fullscreen: Boolean){
        val condition = Video.height > displayMetrics.heightPixels

        Video.layoutParams.height = if (condition) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
        VideoWrapper.layoutParams.height = if (condition && fullscreen) ViewGroup.LayoutParams.MATCH_PARENT else  height
        Video.layoutParams.width = if (condition) ViewGroup.LayoutParams.WRAP_CONTENT else ViewGroup.LayoutParams.MATCH_PARENT
        VideoWrapper.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    }

    private fun setFullscreenFlags() =
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun makeFullScreen(){
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.setBackgroundColor(getColor(R.color.black))
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        setFullscreenFlags()
        if(Video.width > Video.height) this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else VideoWrapper.maxHeight = displayMetrics.heightPixels
        setLayout(ViewGroup.LayoutParams.MATCH_PARENT, true)
        fullscreen = true
    }

    private fun exitFullScreen(){
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        window.decorView.setBackgroundColor(backgroundColor)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, false)
        if(Video.height > Video.width) VideoWrapper.maxHeight = displayMetrics.heightPixels / 2
        fullscreen = false
    }

    override fun onBackPressed() = if (fullscreen) exitFullScreen() else super.onBackPressed()

    private fun setButtonColor(color: Int, button: ImageView) =
        ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(ContextCompat.getColor(applicationContext, color)))

    data class ColorResult(val colorLike: Int, val colorDislike: Int)
    private fun mapColors(reaction: String?): ColorResult{
        return when(reaction){
            REACTION_LIKE -> ColorResult(R.color.like, R.color.white)
            REACTION_DISLIKE -> ColorResult(R.color.white, R.color.dislike)
            else -> ColorResult(R.color.white, R.color.white)
        }
    }
}
