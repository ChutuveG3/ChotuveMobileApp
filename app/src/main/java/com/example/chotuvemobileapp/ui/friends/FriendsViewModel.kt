package com.example.chotuvemobileapp.ui.friends

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource

class FriendsViewModel : ViewModel() {

    private lateinit var prefs: SharedPreferences
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

    fun removePendingFriend(friend: String){
        val oldPendingFriends = pendingFriends.value
        oldPendingFriends!!.remove(friend)
        pendingFriends.postValue(oldPendingFriends)
    }

    fun addFriend(friend: String){
        val oldFriends = friends.value
        oldFriends!!.add(friend)
        removePendingFriend(friend)
        friends.postValue(oldFriends)
    }

    fun setPrefs(preferences: SharedPreferences){
        prefs = preferences
    }
}