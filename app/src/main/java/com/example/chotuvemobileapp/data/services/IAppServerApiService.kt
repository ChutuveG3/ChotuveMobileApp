package com.example.chotuvemobileapp.data.services

import com.example.chotuvemobileapp.data.requests.LoginRequest
import com.example.chotuvemobileapp.data.requests.MessageRequest
import com.example.chotuvemobileapp.data.requests.ThirdPartyLoginRequest
import com.example.chotuvemobileapp.data.requests.TokenLoginRequest
import com.example.chotuvemobileapp.data.requests.reactions.CommentRequest
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

    @POST("users/sessions")
    fun loginThirdParty(@Body request: ThirdPartyLoginRequest) : Call<ResponseBody>

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

    @PATCH("videos/{videoId}/like")
    fun like(@Path("videoId") videoId: String) : Call<ResponseBody>

    @PATCH("videos/{videoId}/unlike")
    fun unlike(@Path("videoId") videoId: String) : Call<ResponseBody>

    @PATCH("videos/{videoId}/dislike")
    fun dislike(@Path("videoId") videoId: String) : Call<ResponseBody>

    @PATCH("videos/{videoId}/undislike")
    fun undislike(@Path("videoId") videoId: String) : Call<ResponseBody>

    @POST("videos/{videoId}/comments")
    fun comment(@Path("videoId") videoId: String, @Body comment: CommentRequest) : Call<ResponseBody>

    @GET("videos/{videoId}")
    fun getVideo(@Path("videoId") videoId: String) : Call<ResponseBody>

    @POST("users/{destUser}/messages")
    fun sendMessage(@Path("destUser") dest: String, @Body message: MessageRequest) : Call<ResponseBody>
}