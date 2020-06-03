package com.example.chotuvemobileapp.data.services

import com.example.chotuvemobileapp.data.users.User
import com.example.chotuvemobileapp.data.users.UserForModification
import com.example.chotuvemobileapp.data.videos.Video
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface IAppServerApiService {

    @POST("users")
    fun registerUser(
        @Body user: User
    ): Call<ResponseBody>

    @POST("videos")
    fun uploadVideo(
        @Body video: Video
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("users/sessions")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseBody>

    @GET("users/me")
    fun getOwnProfile(): Call<ResponseBody>

    @PUT("users/me")
    fun modifyProfile(@Body userInfo: UserForModification): Call<ResponseBody>

    @GET("videos")
    fun getAllVideos(@Query("page") pageNumber: Int,
                     @Query("limit") pageSize: Int): Call<ResponseBody>

    @GET("videos/me")
    fun getMyVideos(@Query("page") pageNumber: Int, @Query("limit") pageSize: Int): Call<ResponseBody>

    @GET("videos/{user}")
    fun getVideosFrom(@Path("user") user: String,
                      @Query("page") pageNumber: Int,
                      @Query("limit") pageSize: Int) : Call<ResponseBody>
}