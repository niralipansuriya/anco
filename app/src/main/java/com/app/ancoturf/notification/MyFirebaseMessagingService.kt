package com.app.ancoturf.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.presentation.splash.SplashActivity
import com.app.ancoturf.utils.Logger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
//        Logger.log("onMessageReceive : ${JSONObject(remoteMessage.data?)}")
        if (remoteMessage?.data?.containsKey("activitydata")) {
            var data: JSONObject = JSONObject(remoteMessage.data.get("activitydata"))
            sendNotification(
                data.optString("title"),
                data.optString("content"),
                data.toString()
            )
        }
    }

    private fun sendNotification(title: String?, message: String?, data: String) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra(Constants.FROM_NOTIFICATION, true)
        intent.putExtra(Constants.NOTIFICATION_DATA, data)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.img_logo)
            .setColor(ContextCompat.getColor(this, R.color.theme_green))
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(fcmToken: String) {
        super.onNewToken(fcmToken)
        Logger.log("New Token : $fcmToken")
        this.getSharedPreferences(SharedPrefs.PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(SharedPrefs.FCM_TOKEN, fcmToken).apply()

    }
}