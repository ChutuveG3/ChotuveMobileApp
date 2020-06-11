package com.example.chotuvemobileapp.data.videos

import android.content.SharedPreferences
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildAuthenticatedClient
import com.example.chotuvemobileapp.entities.VideoItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

object VideoDataSource {

    fun addVideo(video: Video, preferences: SharedPreferences, myCallback: (String) -> Unit){

        val retrofit = buildAuthenticatedClient(preferences)
        // refactor
//        val user = preferences.getString("username", "")
//        retrofit.uploadVideo(user, video).enqueue(object : retrofit2.Callback<ResponseBody>

        retrofit.uploadVideo(video).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke("Failure")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> myCallback.invoke("Success")
                    else ->  myCallback.invoke("ServerError")
                }
            }
        })
    }

    fun getVideosFrom(preferences: SharedPreferences, pageNumber: Int, pageSize: Int, user: String? = null, myCallback: (ArrayList<VideoItem>) -> Unit){

        val retrofit = buildAuthenticatedClient(preferences = preferences)
        val method = when (user) {
            null -> retrofit.getAllVideos(pageNumber, pageSize)
            "Me" -> retrofit.getMyVideos(pageNumber, pageSize)
            else -> retrofit.getVideosFrom(user, pageNumber, pageSize)
            // refactor
            // retrofit.getVideosFrom(user, pageNumber, pageSize)
        }
        val fail = ArrayList<VideoItem>()
        method.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke(fail)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val types = object : TypeToken<ArrayList<VideoItem>>() {}.type
                        val videos = Gson().fromJson<ArrayList<VideoItem>>(response.body()!!.string(), types)
                        myCallback.invoke(videos)
                    }
                    else -> {
                        myCallback.invoke(fail)
                    }
                }
            }
        })
    }

}