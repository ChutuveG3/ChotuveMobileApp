package com.example.chotuvemobileapp.data.videos

import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildClient
import com.example.chotuvemobileapp.entities.VideoItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

object VideoDataSource {

    fun addVideo(video: Video, myCallback: (String) -> Unit){

        val retrofit = buildClient()

        retrofit.uploadVideo(video).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke("Failure")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        myCallback.invoke("Success")
                    }
                    else -> {
                        myCallback.invoke("ServerError")
                    }
                }
            }
        })
    }

    fun getVideosFrom(user: String? = null, myCallback: (ArrayList<VideoItem>) -> Unit){

        val retrofit = buildClient(url = "http://www.mocky.io/v2/5ed021563500007100ff9ae9/")
        val method = if (user != null) retrofit.getVideosFrom(user) else retrofit.getAllVideos()
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