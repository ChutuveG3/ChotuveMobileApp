package com.example.chotuvemobileapp.data.users

import com.example.chotuvemobileapp.data.requests.LoginRequest
import com.example.chotuvemobileapp.data.requests.TokenLoginRequest
import com.example.chotuvemobileapp.data.response.AuthErrorResponse
import com.example.chotuvemobileapp.data.response.LoginResponse
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildClient
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LoginDataSource {

    fun login(username: String, password: String, myCallback: (String) -> Unit){

        val retrofit = buildClient()
        val request = LoginRequest(username, password)

        retrofit.loginUser(request).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke("Failure")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val resp = Gson().fromJson(response.body()!!.string(), LoginResponse::class.java)
                        myCallback.invoke(resp.token)
                    }
                    else -> myCallback.invoke("InvalidParams")
                }
            }
        })
    }

    fun addUser(user : User, myCallback: (String) -> Unit){

        val retrofit = buildClient()

        retrofit.registerUser(user).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke("Failure")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> myCallback.invoke("Success")
                    response.code() == 502 -> {
                        val error = Gson().fromJson(response.errorBody()!!.string(), AuthErrorResponse::class.java)
                        myCallback.invoke(error.message.internal_code)
                    }
                    else -> myCallback.invoke("InvalidParams")
                }
            }
        })
    }

    fun tokenLogin(username: String, password: String, token: String, myCallback: (String) -> Unit){

        val retrofit = buildClient()

        val request = TokenLoginRequest(username, password, token)

        retrofit.tokenLoginUser(request).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke("Failure")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        myCallback.invoke("Success")
                    }
                    else -> myCallback.invoke("InvalidParams")
                }
            }
        })
    }
}



