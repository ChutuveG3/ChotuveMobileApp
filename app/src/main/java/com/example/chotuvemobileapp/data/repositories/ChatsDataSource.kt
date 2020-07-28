package com.example.chotuvemobileapp.data.repositories

import android.content.SharedPreferences
import com.example.chotuvemobileapp.data.requests.MessageRequest
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildAuthenticatedClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object ChatsDataSource {
    fun sendMessage(preferences: SharedPreferences, user: String, message: String){
        val retrofit = buildAuthenticatedClient(preferences)
        val request = MessageRequest(message)

        retrofit.sendMessage(user, request).enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
        })
    }
}