package com.example.chotuvemobileapp.data.repositories

import android.content.SharedPreferences
import com.example.chotuvemobileapp.data.response.VideoListResponse
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildAuthenticatedClient
import com.example.chotuvemobileapp.data.videos.Video
import com.example.chotuvemobileapp.entities.VideoItem
import com.example.chotuvemobileapp.helpers.Utilities.FAILURE_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.SERVER_ERROR_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.SUCCESS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

object VideoDataSource {

    fun addVideo(video: Video, preferences: SharedPreferences, myCallback: (String) -> Unit){

        val retrofit = buildAuthenticatedClient(preferences)

        retrofit.uploadVideo(video).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke(FAILURE_MESSAGE)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> myCallback.invoke(SUCCESS_MESSAGE)
                    else ->  myCallback.invoke(SERVER_ERROR_MESSAGE)
                }
            }
        })
    }

    fun getVideosFrom(preferences: SharedPreferences, pageNumber: Int, pageSize: Int, user: String? = null, myCallback: (ArrayList<VideoItem>) -> Unit){

        val retrofit = buildAuthenticatedClient(preferences)
        val username = preferences.getString(USERNAME, "")!!
        val method = when (user) {
            null -> retrofit.getHomeVideos(username, pageNumber, pageSize)
            else -> retrofit.getVideos(user, pageNumber, pageSize)
        }
        val fail = ArrayList<VideoItem>()
        method.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke(fail)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val videos = Gson().fromJson(response.body()!!.string(), VideoListResponse::class.java)
                        myCallback.invoke(videos.videos)
                    }
                    else -> {
                        myCallback.invoke(fail)
                    }
                }
            }
        })
    }

}