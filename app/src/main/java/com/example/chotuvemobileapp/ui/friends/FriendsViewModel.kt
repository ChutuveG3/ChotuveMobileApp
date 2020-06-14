package com.example.chotuvemobileapp.ui.friends

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import com.example.chotuvemobileapp.viewmodels.ProfileViewModel

class FriendsViewModel(private val prefs: SharedPreferences) : ViewModel() {

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

    companion object{
        private lateinit var instance: FriendsViewModel
        fun getInstance(prefs: SharedPreferences) : FriendsViewModel {
            instance = if (::instance.isInitialized) instance else FriendsViewModel(prefs)
            return instance
        }
    }
}