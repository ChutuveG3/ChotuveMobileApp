package com.example.chotuvemobileapp.data.users

import android.content.Context
import android.provider.Settings.Global.getString
import com.example.chotuvemobileapp.BuildConfig
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.services.IAppServerApiService
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProfileInfoDataSource {
    fun getProfileInfo(token: String, myCallback: (UserInfo?) -> Unit){

        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)
        val client = OkHttpClient.Builder().addInterceptor(Interceptor{
            val original = it.request()
            val request = original.newBuilder().addHeader("authorization", token).build()
            return@Interceptor it.proceed(request)
        }).addInterceptor(logging).build()

        val retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).build().create(IAppServerApiService::class.java)


        retrofit.getOwnProfile().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke(null)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val resp = Gson().fromJson(response.body()!!.string(), UserInfo::class.java)
                        myCallback.invoke(resp)
                    }
                    else -> {
                        myCallback.invoke(null)
                    }
                }
            }
        })
    }
}