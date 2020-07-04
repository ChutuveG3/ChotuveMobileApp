package com.example.chotuvemobileapp.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.data.videos.VideoDataSource
import com.example.chotuvemobileapp.entities.VideoItem

class VideosViewModel : ViewModel() {
    private lateinit var prefs: SharedPreferences
    private var user: String? = null
    var loadedAllVideos = false
    private val pageSize = 10
    private var currentPage = 1

    val videos by lazy {
        val liveData = MutableLiveData<ArrayList<VideoItem>>()
        VideoDataSource.getVideosFrom(prefs, currentPage, pageSize, user){
            liveData.value = it
        }
        return@lazy liveData
    }

    fun getMoreVideos(recyclerView: RecyclerView){
        if (!loadedAllVideos){
            currentPage += 1
            VideoDataSource.getVideosFrom(prefs, currentPage, pageSize, user) {
                if (it.isEmpty()){
                    loadedAllVideos = true
                    return@getVideosFrom
                }
                videos.value!!.addAll(it)
                recyclerView.adapter!!.notifyItemRangeInserted(recyclerView.adapter!!.itemCount, it.count())
            }
        }
    }

    fun setPrefs(preferences: SharedPreferences, username: String?){
        prefs = preferences
        user = username
    }
}