package com.example.chotuvemobileapp.data.repositories

import android.content.SharedPreferences
import com.example.chotuvemobileapp.data.utilities.HttpUtilities
import com.example.chotuvemobileapp.helpers.Utilities
import com.example.chotuvemobileapp.helpers.Utilities.FAILURE_MESSAGE
import com.example.chotuvemobileapp.helpers.VideoReaction
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ReactionsDataSource {
    fun reactToVideo(preferences: SharedPreferences, videoId: String, reaction: VideoReaction, myCallback: (String) -> Unit){
        val retrofit = HttpUtilities.buildAuthenticatedClient(preferences)

        val request = when(reaction){
            VideoReaction.Like -> retrofit.like(videoId)
            VideoReaction.Unlike -> retrofit.unlike(videoId)
            VideoReaction.Dislike -> retrofit.dislike(videoId)
            else -> retrofit.undislike(videoId)
        }
        request.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) = myCallback.invoke(FAILURE_MESSAGE)
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when {
                    response.isSuccessful -> myCallback.invoke(Utilities.SUCCESS_MESSAGE)
                    else -> myCallback.invoke(FAILURE_MESSAGE)
                }
            }
        })
    }
}