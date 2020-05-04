package com.example.chotuvemobileapp

import com.google.gson.Gson
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.lang.StringBuilder


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
object LoginDataSource {

    private const val baseUrl = "https://chotuve-app-server-develop.herokuapp.com"
    private val users = HashMap<String, User>()
    private val client = OkHttpClient.Builder().addInterceptor{chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder().method(original.method, original.body)
        val request = requestBuilder.build()
        chain.proceed(request)
    }.build()
    val source: ILoginDataSource by lazy {
        val retrofit = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).build()
        retrofit.create(ILoginDataSource::class.java)
    }

    fun login(username: String, password: String): Result {
        if (!users.containsKey(username)) return Result(false, Error.UserNotRegistered)
        val user = users[username]
        if (user!!.password != password) return Result(false, Error.IncorrectPassword)

        return Result(true, null)
    }

    fun getUsersName(username: String): String{
        return users[username]!!.first_name
    }

}


