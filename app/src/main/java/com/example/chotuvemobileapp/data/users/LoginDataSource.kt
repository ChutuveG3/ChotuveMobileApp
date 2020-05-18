package com.example.chotuvemobileapp.data.users

import com.example.chotuvemobileapp.BuildConfig
import com.example.chotuvemobileapp.data.response.AuthErrorResponse
import com.example.chotuvemobileapp.data.services.IAppServerApiService
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
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
    private val users = HashMap<String, User>()

    fun login(username: String, password: String): Result {
        if (!users.containsKey(username)) return Result(
            false,
            Error.UserNotRegistered
        )
        val user = users[username]
        if (user!!.password != password) return Result(
            false,
            Error.IncorrectPassword
        )

        return Result(true, null)
    }

    fun getUsersName(username: String): String{
        return users[username]!!.first_name
    }

    fun addUser(user : User, myCallback: (String) -> Unit){

        val client = OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .build()

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
                    response.code() == 500 -> {
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

