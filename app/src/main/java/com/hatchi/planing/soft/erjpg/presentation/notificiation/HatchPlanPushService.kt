package com.hatchi.planing.soft.erjpg.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hatchi.planing.soft.HatchPlanActivity
import com.hatchi.planing.soft.R
import com.hatchi.planing.soft.erjpg.presentation.app.HatchPlanApplication

private const val HATCH_PLAN_CHANNEL_ID = "hatch_plan_notifications"
private const val HATCH_PLAN_CHANNEL_NAME = "HatchPlan Notifications"
private const val HATCH_PLAN_NOT_TAG = "HatchPlan"

class HatchPlanPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                hatchPlanShowNotification(it.title ?: HATCH_PLAN_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                hatchPlanShowNotification(it.title ?: HATCH_PLAN_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            hatchPlanHandleDataPayload(remoteMessage.data)
        }
    }

    private fun hatchPlanShowNotification(title: String, message: String, data: String?) {
        val hatchPlanNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                HATCH_PLAN_CHANNEL_ID,
                HATCH_PLAN_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            hatchPlanNotificationManager.createNotificationChannel(channel)
        }

        val hatchPlanIntent = Intent(this, HatchPlanActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val hatchPlanPendingIntent = PendingIntent.getActivity(
            this,
            0,
            hatchPlanIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val hatchPlanNotification = NotificationCompat.Builder(this, HATCH_PLAN_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_hatch_plan_noti)
            .setAutoCancel(true)
            .setContentIntent(hatchPlanPendingIntent)
            .build()

        hatchPlanNotificationManager.notify(System.currentTimeMillis().toInt(), hatchPlanNotification)
    }

    private fun hatchPlanHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}