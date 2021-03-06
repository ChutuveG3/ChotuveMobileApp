package com.example.chotuvemobileapp.data.repositories

import com.example.chotuvemobileapp.data.requests.ThirdPartyLoginRequest
import com.example.chotuvemobileapp.data.requests.TokenLoginRequest
import com.example.chotuvemobileapp.data.response.AuthErrorResponse
import com.example.chotuvemobileapp.data.response.LoginResponse
import com.example.chotuvemobileapp.data.users.User
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildClient
import com.example.chotuvemobileapp.helpers.ThirdPartyLoginResult
import com.example.chotuvemobileapp.helpers.Utilities.FAILURE_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.INVALID_PARAMS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.SUCCESS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.USER_NOT_REGISTERED
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LoginDataSource {

    fun loginWithThirdParty(token: String, deviceToken: String, myCallback: (ThirdPartyLoginResult) -> Unit){

        val retrofit = buildClient()
        val request = ThirdPartyLoginRequest(token, deviceToken)

        retrofit.loginThirdParty(request).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke(ThirdPartyLoginResult(FAILURE_MESSAGE, "Fail", "Fail"))
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val resp = Gson().fromJson(response.body()!!.string(), LoginResponse::class.java)
                        myCallback.invoke(ThirdPartyLoginResult(SUCCESS_MESSAGE, resp.username, resp.token))
                    }
                    response.code() == 409 -> myCallback.invoke(ThirdPartyLoginResult(USER_NOT_REGISTERED, "Fail", "Fail"))
                    else -> myCallback.invoke(ThirdPartyLoginResult(INVALID_PARAMS_MESSAGE, "Fail", "Fail"))
                }
            }
        })
    }

    fun addUser(user : User, myCallback: (String) -> Unit){

        val retrofit = buildClient()

        retrofit.registerUser(user).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke(FAILURE_MESSAGE)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> myCallback.invoke(SUCCESS_MESSAGE)
                    response.code() == 502 -> {
                        val error = Gson().fromJson(response.errorBody()!!.string(), AuthErrorResponse::class.java)
                        myCallback.invoke(error.message.internal_code)
                    }
                    else -> myCallback.invoke(INVALID_PARAMS_MESSAGE)
                }
            }
        })
    }

    fun tokenLogin(username: String, password: String, token: String, myCallback: (String) -> Unit){

        val retrofit = buildClient()

        val request = TokenLoginRequest(username, password, token)

        retrofit.tokenLoginUser(request).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke(FAILURE_MESSAGE)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val resp = Gson().fromJson(response.body()!!.string(), LoginResponse::class.java)
                        myCallback.invoke(resp.token)
                    }
                    else -> myCallback.invoke(INVALID_PARAMS_MESSAGE)
                }
            }
        })
    }
}



