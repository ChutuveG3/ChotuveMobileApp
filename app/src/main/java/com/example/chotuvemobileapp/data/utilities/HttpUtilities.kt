package com.example.chotuvemobileapp.data.utilities

import android.content.SharedPreferences
import com.example.chotuvemobileapp.BuildConfig
import com.example.chotuvemobileapp.data.services.IAppServerApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object HttpUtilities {

    private fun makeBasicClient(): OkHttpClient.Builder{
        return OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .callTimeout(3, TimeUnit.MINUTES)
    }

    fun buildAuthenticatedClient(preferences: SharedPreferences, url: String = BuildConfig.BASE_URL): IAppServerApiService{
        val headers = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = makeBasicClient()
        val token = preferences.getString("token", "")
        val interceptor = Interceptor {
            val original = it.request()
            val request = original.newBuilder().addHeader("authorization", token!!).build()
            return@Interceptor it.proceed(request)
        }

        val builtClient = client.authenticator(TokenAuthenticator(preferences))
            .addInterceptor(interceptor)
            .addInterceptor(headers).build()

        return Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(builtClient).build().create(IAppServerApiService::class.java)
    }

    fun buildClient(url: String = BuildConfig.BASE_URL): IAppServerApiService{
        val client = makeBasicClient()
        val body = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.addInterceptor(body).build()).build().create(IAppServerApiService::class.java)
    }

    fun buildFirebaseTokenClient(url: String = BuildConfig.BASE_URL, token: String): IAppServerApiService{
        val client = makeBasicClient()
        val interceptor = Interceptor{
            val original = it.request()
            val request = original.newBuilder().addHeader("firebase_token", token).build()
            return@Interceptor it.proceed(request)
        }
        val body = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)
        return Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.addInterceptor(body).addInterceptor(interceptor).build()).build().create(IAppServerApiService::class.java)
    }
}