package com.example.chotuvemobileapp.data.videos

import com.example.chotuvemobileapp.BuildConfig
import com.example.chotuvemobileapp.data.services.IAppServerApiService
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object VideoDataSource {

    fun addVideo(video: Video, myCallback: (String) -> Unit){
        val client = OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS).build()

        val retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).build().create(IAppServerApiService::class.java)

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
}