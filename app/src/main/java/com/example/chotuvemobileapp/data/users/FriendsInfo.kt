package com.example.chotuvemobileapp.data.users

import androidx.lifecycle.MutableLiveData

object FriendsInfo {
    val needsUpdate by lazy {
        val liveData = MutableLiveData<Boolean>()
        liveData.value = false
        return@lazy liveData
    }
}