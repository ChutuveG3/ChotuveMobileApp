package com.example.chotuvemobileapp.data.utilities

import com.example.chotuvemobileapp.BuildConfig
import com.example.chotuvemobileapp.data.services.IAppServerApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object HttpUtilities {

    fun buildClient(interceptor: Interceptor? = null, url: String = BuildConfig.BASE_URL): IAppServerApiService{
        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .callTimeout(3, TimeUnit.MINUTES)
            .addInterceptor(logging)

        if (interceptor != null) client.addInterceptor(interceptor)

        val builtClient = client.build()

        return Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(builtClient).build().create(IAppServerApiService::class.java)
    }
}