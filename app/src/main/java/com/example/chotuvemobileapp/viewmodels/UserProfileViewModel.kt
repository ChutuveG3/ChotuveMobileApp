package com.example.chotuvemobileapp.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import com.example.chotuvemobileapp.data.users.UserInfo

class UserProfileViewModel : ViewModel(){

    private lateinit var username: String
    private lateinit var prefs: SharedPreferences

    val userInfo by lazy {
        val liveData = MutableLiveData<UserInfo>()
        ProfileInfoDataSource.getProfileInfo(prefs, username){
            liveData.value = it
        }
        return@lazy liveData
    }

    val friends by lazy {
        val liveData = MutableLiveData<ArrayList<String>>()
        ProfileInfoDataSource.getFriends(prefs, prefs.getString("username", "")!!){
            liveData.value = it
        }
        return@lazy liveData
    }
    val pendingFriends by lazy {
        val liveData = MutableLiveData<ArrayList<String>>()
        ProfileInfoDataSource.getPendingFriends(prefs, prefs.getString("username", "")!!){
            liveData.value = it
        }
        return@lazy liveData
    }

    fun setPrefs(preferences: SharedPreferences){
        prefs = preferences
    }

    fun setUser(user: String){
        username = user
    }
}