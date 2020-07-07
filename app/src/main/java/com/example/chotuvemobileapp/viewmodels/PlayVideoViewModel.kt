package com.example.chotuvemobileapp.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chotuvemobileapp.data.repositories.VideoDataSource
import com.example.chotuvemobileapp.data.videos.VideoInfo
import com.example.chotuvemobileapp.entities.CommentItem

class PlayVideoViewModel : ViewModel() {
    private lateinit var prefs: SharedPreferences
    val comments by lazy {
        val liveData = MutableLiveData<ArrayList<CommentItem>>()
        val dummyComments = ArrayList<CommentItem>()
        for (i in 1..5) {
            dummyComments.add(CommentItem("Comentario hardcodeado #$i.\nHardcodear es malo", "User $i", "$i/$i/$i"))
        }
        liveData.value = dummyComments
        return@lazy liveData
    }

    val video by lazy {
        val liveData = MutableLiveData<VideoInfo>()
        VideoDataSource.getVideo(prefs, videoId){
            liveData.value = it
        }
        return@lazy liveData
    }

    lateinit var videoId: String
    var liked = false
    var disliked = false

    var likes = 32525
    var dislikes = 2

    var descriptionExpanded = false

    fun setPrefs(preferences: SharedPreferences){
        prefs = preferences
    }
}