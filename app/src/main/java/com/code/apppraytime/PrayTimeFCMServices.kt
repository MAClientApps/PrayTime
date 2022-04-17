package com.code.apppraytime

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustEvent
import com.code.apppraytime.screen.SplashActivity
import com.code.apppraytime.utils.KEY_PUSH_TOKN
import com.code.apppraytime.utils.generateUserUUID
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PrayTimeFCMServices : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        val adjustEvent = AdjustEvent(KEY_PUSH_TOKN)
        adjustEvent.addCallbackParameter("eventValue", s)
        adjustEvent.addCallbackParameter(
            "user_uuid",
            generateUserUUID(this@PrayTimeFCMServices)
        )
        Adjust.trackEvent(adjustEvent)
        Adjust.setPushToken(s, this@PrayTimeFCMServices)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val notification = remoteMessage.notification
        val data = remoteMessage.data
        NotificationSend(notification, data)
    }

    private fun NotificationSend(
        notification: RemoteMessage.Notification?,
        data: Map<String, String>?
    ) {
        var intent: Intent? = null
        var title: String? = ""
        var body: String? = ""
        if (data != null) {
            val action = data["action_id"]
            val deeplink = data["deeplink"]
            if (action != null && action.equals("1", ignoreCase = true)) {
                intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            } else {
                intent = Intent(this, SplashActivity::class.java)
            }
        } else {
            intent = Intent(this, SplashActivity::class.java)
        }
        if (notification != null) {
            title = notification.title
            body = notification.body
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notificationBuilder = NotificationCompat.Builder(this, "1")
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.mipmap.ic_launcher
                )
            )
            .setSmallIcon(R.mipmap.ic_launcher_round)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "1", CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESC
            channel.setShowBadge(true)
            channel.canShowBadge()
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            assert(notificationManager != null)
            notificationManager.createNotificationChannel(channel)
        }
        assert(notificationManager != null)
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val CHANNEL_NAME = "FCM"
        private const val CHANNEL_DESC = "FirebaseService"
    }
}
