package com.example.chotuvemobileapp.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chotuvemobileapp.data.repositories.VideoDataSource
import com.example.chotuvemobileapp.data.videos.VideoInfo
import com.example.chotuvemobileapp.entities.CommentItem

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
}