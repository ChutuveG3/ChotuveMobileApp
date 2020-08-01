package com.example.chotuvemobileapp.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.chotuvemobileapp.data.repositories.FriendsDataSource
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME

class FriendsViewModel : ViewModel() {

    private lateinit var prefs: SharedPreferences
    val friends by lazy {
        val liveData = MutableLiveData<ArrayList<String>>()
        FriendsDataSource.getFriends(prefs, prefs.getString(USERNAME, "")!!){
            liveData.value = it
        }
        return@lazy liveData
    }

    val pendingFriends by lazy {
        val liveData = MutableLiveData<ArrayList<String>>()
        FriendsDataSource.getPendingFriends(prefs, prefs.getString(USERNAME, "")!!){
            liveData.value = it
        }
        return@lazy liveData
    }

    fun updateFriends(refreshLayout: SwipeRefreshLayout){
        FriendsDataSource.getFriends(prefs, prefs.getString(USERNAME, "")!!){
            friends.postValue(it)
            refreshLayout.isRefreshing = false
        }
        FriendsDataSource.getPendingFriends(prefs, prefs.getString(USERNAME, "")!!){
            pendingFriends.postValue(it)
            refreshLayout.isRefreshing = false
        }
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