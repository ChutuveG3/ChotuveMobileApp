package com.example.chotuvemobileapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.chotuvemobileapp.data.users.LoginDataSource
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.android.synthetic.main.activity_login.*


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val prefs by lazy {
        getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }

    override fun onNewToken(token: String) {
        Log.d("NEW_FIREBASE_TOKEN", token)
        val username = prefs.getString("username", "")
        val pass = prefs.getString("password", "")

        LoginDataSource.tokenLogin(username!!, pass!!, token) {
            when (it) {
                "Success" -> Log.d("NEW_FIREBASE_TOKEN", "token update!")
                else -> Log.d("NEW_FIREBASE_TOKEN", "fail token update")
            }
        }
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
    }
}

