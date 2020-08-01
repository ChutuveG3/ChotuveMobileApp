package com.example.chotuvemobileapp.data.repositories

import android.content.SharedPreferences
import com.example.chotuvemobileapp.data.response.PotentialFriendsResponse
import com.example.chotuvemobileapp.data.users.Friends
import com.example.chotuvemobileapp.data.users.PendingFriends
import com.example.chotuvemobileapp.data.utilities.HttpUtilities
import com.example.chotuvemobileapp.helpers.Utilities
import com.example.chotuvemobileapp.helpers.Utilities.FAILURE_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.SUCCESS_MESSAGE
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FriendsDataSource {
    fun getFriends(preferences: SharedPreferences, user: String, myCallback: (ArrayList<String>) -> Unit){

        val fail = ArrayList<String>()
        val retrofit = HttpUtilities.buildAuthenticatedClient(preferences)

        retrofit.getFriends(user).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) = myCallback.invoke(fail)

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
        val retrofit = HttpUtilities.buildAuthenticatedClient(preferences)

        retrofit.getPendingFriends(user).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) = myCallback.invoke(fail)

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val resp = Gson().fromJson(response.body()!!.string(), PendingFriends::class.java)
                        myCallback.invoke(resp.friend_requests)
                    }
                    else -> myCallback.invoke(fail)
                }
            }
        })
    }

    fun respondRequest(preferences: SharedPreferences, friend: String, accept: Boolean, myCallback: (String) -> Unit){
        val retrofit = HttpUtilities.buildAuthenticatedClient(preferences)
        val user = preferences.getString(Utilities.USERNAME, "")!!
        val request = if (accept) retrofit.acceptFriendRequest(user, friend)
        else retrofit.declineFriendRequest(user, friend)
        request.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) = myCallback.invoke(FAILURE_MESSAGE)

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> myCallback.invoke(SUCCESS_MESSAGE)
                    else -> myCallback.invoke(FAILURE_MESSAGE)
                }
            }
        })
    }

    fun addFriend(preferences: SharedPreferences, friend: String, myCallback: (String) -> Unit){
        val retrofit = HttpUtilities.buildAuthenticatedClient(preferences)
        val user = preferences.getString(Utilities.USERNAME, "")!!
        retrofit.sendFriendRequest(user, friend).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) = myCallback.invoke(FAILURE_MESSAGE)

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> myCallback.invoke(SUCCESS_MESSAGE)
                    else -> myCallback.invoke(FAILURE_MESSAGE)
                }
            }
        })
    }

    fun getSimilarUsers(preferences: SharedPreferences, query: String, myCallback: (ArrayList<String>?) -> Unit){
        val retrofit = HttpUtilities.buildAuthenticatedClient(preferences)
        val user = preferences.getString(Utilities.USERNAME, "")!!
        retrofit.searchFriends(user, query).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) = myCallback.invoke(null)

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        val resp = Gson().fromJson(response.body()!!.string(), PotentialFriendsResponse::class.java)
                        myCallback.invoke(resp.potentialFriends)
                    }
                    else -> myCallback.invoke(null)
                }
            }
        })
    }
}