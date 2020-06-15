package com.example.chotuvemobileapp.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import com.example.chotuvemobileapp.data.users.UserInfo

class ProfileViewModel(private val prefs: SharedPreferences) : ViewModel() {

    val userInfo by lazy {
        val liveData = MutableLiveData<UserInfo>()
        ProfileInfoDataSource.getProfileInfo(prefs, prefs.getString("username", "")!!){
            liveData.value = it
        }
        return@lazy liveData
    }

    fun getUserInfo(): LiveData<UserInfo> = userInfo
    fun updateUserInfo(firstName: String, lastName: String, dateOfBirth: String, email: String){
        val oldUser = userInfo.value
        val newUser = UserInfo(oldUser!!.user_name, firstName, lastName, email, dateOfBirth)
        userInfo.postValue(newUser)
    }

    companion object{
        private lateinit var instance: ProfileViewModel
        fun getInstance(prefs: SharedPreferences) : ProfileViewModel{
            instance = if (::instance.isInitialized) instance else ProfileViewModel(prefs)
            return instance
        }
    }
}