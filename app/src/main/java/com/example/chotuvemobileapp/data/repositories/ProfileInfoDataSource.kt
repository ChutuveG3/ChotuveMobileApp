package com.example.chotuvemobileapp.data.repositories

import android.content.SharedPreferences
import com.example.chotuvemobileapp.data.users.UserForModification
import com.example.chotuvemobileapp.data.users.UserInfo
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildAuthenticatedClient
import com.example.chotuvemobileapp.helpers.Utilities.FAILURE_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.SERVER_ERROR_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.SUCCESS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ProfileInfoDataSource {
    fun getProfileInfo(preferences: SharedPreferences, user: String, myCallback: (UserInfo?) -> Unit){
        val retrofit = buildAuthenticatedClient(preferences)

        retrofit.getProfile(user).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) = myCallback.invoke(null)

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val resp = Gson().fromJson(response.body()!!.string(), UserInfo::class.java)
                        myCallback.invoke(resp)
                    }
                    else -> myCallback.invoke(null)
                }
            }
        })
    }

    fun modifyProfileInfo(preferences: SharedPreferences, userInfo: UserForModification, myCallback: (String) -> Unit){
        val retrofit = buildAuthenticatedClient(preferences)
        val user = preferences.getString(USERNAME, "")!!

        retrofit.modifyProfile(user, userInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) = myCallback.invoke(FAILURE_MESSAGE)

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                myCallback.invoke(when {
                    response.isSuccessful -> SUCCESS_MESSAGE
                    response.code() == 502 -> "EmailInvalid"
                    else -> SERVER_ERROR_MESSAGE
                })
            }
        })
    }
}