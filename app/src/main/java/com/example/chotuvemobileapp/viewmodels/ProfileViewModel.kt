package com.example.chotuvemobileapp.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import com.example.chotuvemobileapp.data.users.UserInfo

class ProfileViewModel : ViewModel() {

    private lateinit var prefs: SharedPreferences
    val userInfo by lazy {
        val liveData = MutableLiveData<UserInfo>()
        ProfileInfoDataSource.getProfileInfo(prefs, prefs.getString("username", "")!!){
            liveData.value = it
        }
        return@lazy liveData
    }
    fun setPrefs(preferences: SharedPreferences){
        prefs = preferences
    }

    fun updateUserInfo(firstName: String, lastName: String, dateOfBirth: String, email: String, pic_url: String?){
        val oldUser = userInfo.value
        val newUser = UserInfo(oldUser!!.user_name, firstName, lastName, email, dateOfBirth, pic_url, oldUser.friendship)
        userInfo.postValue(newUser)
    }
}