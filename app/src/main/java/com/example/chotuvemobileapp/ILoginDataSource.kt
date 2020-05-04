package com.example.chotuvemobileapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ILoginDataSource {

    @POST("users")
    fun registerUser(
        @Body info: User
    ): Call<ResponseBody>
}