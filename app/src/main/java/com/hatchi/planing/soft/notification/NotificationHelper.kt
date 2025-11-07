package com.hatchi.planing.soft.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hatchi.planing.soft.MainActivity
import com.hatchi.planing.soft.R
import com.hatchi.planing.soft.data.entity.Task
import com.hatchi.planing.soft.util.TaskUtils

class NotificationHelper(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "hatchplan_tasks"
        private const val CHANNEL_NAME = "Task Reminders"
        private const val CHANNEL_DESCRIPTION = "Notifications for egg turning, candling, and other tasks"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendTaskNotification(task: Task, batchName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val taskName = TaskUtils.getTaskDisplayName(task.type)
        val taskIcon = TaskUtils.getTaskIcon(task.type)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_today) // Using system icon as fallback
            .setContentTitle("$taskIcon $taskName")
            .setContentText("Time for $taskName - $batchName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(task.id.toInt(), notification)
        } catch (e: SecurityException) {
            // Permission not granted, handle silently
        }
    }

    fun cancelTaskNotification(taskId: Long) {
        NotificationManagerCompat.from(context).cancel(taskId.toInt())
    }
}

