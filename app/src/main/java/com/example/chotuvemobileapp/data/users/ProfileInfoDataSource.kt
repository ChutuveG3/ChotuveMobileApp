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

        retrofit.getOwnProfile().enqueue(object : Callback<ResponseBody> {
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
}