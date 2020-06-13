package com.example.chotuvemobileapp.data.users

import android.content.SharedPreferences
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildAuthenticatedClient
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ProfileInfoDataSource {
    fun getProfileInfo(preferences: SharedPreferences, myCallback: (UserInfo?) -> Unit){

        val retrofit = buildAuthenticatedClient(preferences)
        val user: String? = preferences.getString("username", "")

        retrofit.getProfile(user!!).enqueue(object : Callback<ResponseBody> {
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

    fun modifyProfileInfo(preferences: SharedPreferences, userInfo: UserForModification, myCallback: (String) -> Unit){

        val retrofit = buildAuthenticatedClient(preferences)
        // refactor
//        val user = preferences.getString("username", "")
//        retrofit.modifyProfile(user, userInfo).enqueue(object : Callback<ResponseBody> {

        retrofit.modifyProfile(userInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke("Failure")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> myCallback.invoke("Success")
                    response.code() == 502 -> myCallback.invoke("EmailInvalid")
                    else -> myCallback.invoke("ServerError")
                }
            }
        })
    }
}