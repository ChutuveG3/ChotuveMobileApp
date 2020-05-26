package com.example.chotuvemobileapp.data.users

import com.example.chotuvemobileapp.BuildConfig
import com.example.chotuvemobileapp.data.response.AuthErrorResponse
import com.example.chotuvemobileapp.data.response.LoginResponse
import com.example.chotuvemobileapp.data.services.IAppServerApiService
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildClient
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
object LoginDataSource {

    fun login(email: String, password: String, myCallback: (String) -> Unit){

        val retrofit = buildClient()

        retrofit.loginUser(email, password).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke("Failure")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val resp = Gson().fromJson(response.body()!!.string(), LoginResponse::class.java)
                        myCallback.invoke(resp.token)
                    }
                    else -> {
                        myCallback.invoke("InvalidParams")
                    }
                }
            }
        })
    }

    fun addUser(user : User, myCallback: (String) -> Unit){

        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .callTimeout(3, TimeUnit.MINUTES)
            .addInterceptor(logging).build()

        val retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).build().create(IAppServerApiService::class.java)

        retrofit.registerUser(user).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke("Failure")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        myCallback.invoke("Success")
                    }
                    response.code() == 502 -> {
                        val error = Gson().fromJson(response.errorBody()!!.string(), AuthErrorResponse::class.java)
                        myCallback.invoke(error.message.internal_code)
                    }
                    else -> {
                        myCallback.invoke("InvalidParams")
                    }
                }
            }
        })
    }
}

