package com.example.chotuvemobileapp

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chotuvemobileapp.data.repositories.LoginDataSource
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val prefs by lazy {
        getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }

    private var notificationId = 0

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

    override fun onMessageReceived(message: RemoteMessage) {

        super.onMessageReceived(message)
        val notiBuilder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setChannelId(getString(R.string.default_notification_channel_id))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId, notiBuilder.build())
        }

        notificationId += 1
    }
}

