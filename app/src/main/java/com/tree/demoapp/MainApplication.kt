package com.tree.demoapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.w("Firebase token", "Fetching FCM registration token failed", it.exception)
            }
            val token = it.result
            Log.d("Token", token.toString())
            getSharedPreferences("Key_Encrypt", Context.MODE_PRIVATE)
                .edit()
                .putString("fcm_token", token)
                .apply()
        }

        registerChannel()
    }

    private fun registerChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            this.resources.getString(R.string.default_notification_channel_id),
            this.resources.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.description = this.resources.getString(R.string.channel_description)
        channel.setShowBadge(true)
        channel.canShowBadge()
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
        notificationManager.createNotificationChannel(channel)
    }
}