package com.example.chotuvemobileapp.data.services

import com.example.chotuvemobileapp.data.requests.LoginRequest
import com.example.chotuvemobileapp.data.requests.TokenLoginRequest
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

//    refactor
    @POST("/videos")
    fun uploadVideo(@Body video: Video): Call<ResponseBody>

    @POST("users/sessions")
    fun loginUser(
        @Body request: LoginRequest
    ): Call<ResponseBody>
    
    @POST("users/sessions")
    fun tokenLoginUser(
        @Body request: TokenLoginRequest
    ): Call<ResponseBody>

    @DELETE("users/{user}/sessions")
    fun logoutUser(@Path("user") user: String): Call<ResponseBody>
    
    @GET("users/{user}")
    fun getProfile(@Path("user") user: String): Call<ResponseBody>

    @PUT("users/{user}")
    fun modifyProfile(@Path("user") user: String,
                      @Body userInfo: UserForModification): Call<ResponseBody>

    @GET("users/{user}/home")
    fun getHomeVideos(@Path("user") user: String,
                    @Query("page") pageNumber: Int,
                    @Query("limit") pageSize: Int): Call<ResponseBody>

    @GET("users/{user}/videos")
    fun getVideos(@Path("user") user: String,
                    @Query("page") pageNumber: Int,
                    @Query("limit") pageSize: Int): Call<ResponseBody>
    @GET("users/{user}/friends")
    fun getFriends(@Path("user") user: String) : Call<ResponseBody>

    @GET("users/{user}/friends/pending")
    fun getPendingFriends(@Path("user") user: String) : Call<ResponseBody>

    @POST("users/{orig}/friends/{dest}")
    fun sendFriendRequest(@Path("orig") sender: String,
                          @Path("dest") destination: String) : Call<ResponseBody>

    @POST("users/{orig}/friends/{dest}/accept")
    fun acceptFriendRequest(@Path("orig") sender: String,
                            @Path("dest") destination: String) : Call<ResponseBody>

    @POST("users/{orig}/friends/{dest}/reject")
    fun declineFriendRequest(@Path("orig") sender: String,
                             @Path("dest") destination: String) : Call<ResponseBody>

    @GET("users/{user}/potential_friends")
    fun searchFriends(@Path("user") sender: String, @Query("username") queryString: String) : Call<ResponseBody>
}