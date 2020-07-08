package com.example.chotuvemobileapp.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chotuvemobileapp.data.repositories.VideoDataSource
import com.example.chotuvemobileapp.data.videos.VideoInfo
import com.example.chotuvemobileapp.entities.CommentItem
import com.example.chotuvemobileapp.helpers.Utilities.REACTION_DISLIKE
import com.example.chotuvemobileapp.helpers.Utilities.REACTION_LIKE
import com.example.chotuvemobileapp.helpers.VideoReaction

class PlayVideoViewModel : ViewModel() {
    private lateinit var prefs: SharedPreferences
    lateinit var videoId: String

    val comments by lazy {
        MutableLiveData<ArrayList<CommentItem>>()
    }

    val video by lazy {
        val liveData = MutableLiveData<VideoInfo>()
        VideoDataSource.getVideo(prefs, videoId){
            liveData.value = it
            if (it != null ) comments.postValue(it.comments)
        }
        return@lazy liveData
    }

    var descriptionExpanded = false

    fun setValues(preferences: SharedPreferences, id: String){
        prefs = preferences
        videoId = id
    }

    fun addComment(commentItem: CommentItem){
        val newComments = comments.value
        newComments?.add(commentItem)
        comments.postValue(newComments)
    }

    fun react(reaction: VideoReaction){
        val newVideo = video.value!!
        when (reaction) {
            VideoReaction.Like -> {
                if (newVideo.reaction == REACTION_DISLIKE) newVideo.dislikes -= 1
                newVideo.likes += 1
                newVideo.reaction = REACTION_LIKE
            }
            VideoReaction.Unlike -> {
                newVideo.likes -= 1
                newVideo.reaction = null
            }
            VideoReaction.Dislike -> {
                if (newVideo.reaction == REACTION_LIKE) newVideo.likes -= 1
                newVideo.dislikes += 1
                newVideo.reaction = REACTION_DISLIKE
            }
            VideoReaction.Undislike -> {
                newVideo.dislikes -= 1
                newVideo.reaction = null
            }
        }
        video.postValue(newVideo)
    }
}