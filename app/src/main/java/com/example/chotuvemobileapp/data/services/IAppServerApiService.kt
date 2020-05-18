package com.example.chotuvemobileapp.data.services

import com.example.chotuvemobileapp.data.users.User
import com.example.chotuvemobileapp.data.videos.Video
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IAppServerApiService {

    @POST("users")
    fun registerUser(
        @Body user: User
    ): Call<ResponseBody>

    @POST("videos")
    fun uploadVideo(
        @Body video: Video
    ): Call<ResponseBody>
}