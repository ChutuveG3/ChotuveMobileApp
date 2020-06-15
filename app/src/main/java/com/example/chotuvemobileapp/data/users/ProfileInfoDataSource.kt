package com.example.chotuvemobileapp.data.users

import android.content.SharedPreferences
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildAuthenticatedClient
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ProfileInfoDataSource {
    fun getProfileInfo(preferences: SharedPreferences, user: String, myCallback: (UserInfo?) -> Unit){

        val retrofit = buildAuthenticatedClient(preferences)

        retrofit.getProfile(user).enqueue(object : Callback<ResponseBody> {
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

    fun getFriends(preferences: SharedPreferences, user: String, myCallback: (ArrayList<String>) -> Unit){

        val fail = ArrayList<String>()
        val retrofit = buildAuthenticatedClient(preferences)

        retrofit.getFriends(user).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke(fail)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val resp = Gson().fromJson(response.body()!!.string(), Friends::class.java)
                        myCallback.invoke(resp.friends)
                    }
                    else -> {
                        myCallback.invoke(fail)
                    }
                }
            }
        })
    }

    fun getPendingFriends(preferences: SharedPreferences, user: String, myCallback: (ArrayList<String>) -> Unit){

        val fail = ArrayList<String>()
        val retrofit = buildAuthenticatedClient(preferences)

        retrofit.getPendingFriends(user).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke(fail)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val resp = Gson().fromJson(response.body()!!.string(), PendingFriends::class.java)
                        myCallback.invoke(resp.friend_requests)
                    }
                    else -> {
                        myCallback.invoke(fail)
                    }
                }
            }
        })
    }
}