package com.example.musicapp.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.musicapp.MainActivity
import com.example.musicapp.R
import com.example.musicapp.model.DownloadedTrack

class DownloadNotifications(
  private val context: Context,
) {
  init {
    createChannel()
  }

  fun notifySuccess(track: DownloadedTrack) {
    notify(context.getString(R.string.completed), track.name)
  }

  fun notifyFailure(message: String) {
    notify(context.getString(R.string.failed), message)
  }

  @SuppressLint("MissingPermission")
  private fun notify(
    title: String,
    text: String,
  ) {
    if (!canNotify()) return

    val intent =
      Intent(context, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    val pendingIntent =
      PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
      )

    val notification =
      NotificationCompat
        .Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_music_note)
        .setContentTitle(title)
        .setContentText(text)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
  }

  private fun canNotify(): Boolean {
    if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS,
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      return false
    }
    return true
  }

  private fun createChannel() {
    val manager = context.getSystemService(NotificationManager::class.java)
    val channel =
      NotificationChannel(
        CHANNEL_ID,
        context.getString(R.string.app_name),
        NotificationManager.IMPORTANCE_DEFAULT,
      ).apply {
        description = context.getString(R.string.completed)
      }
    manager.createNotificationChannel(channel)
  }

  private companion object {
    const val CHANNEL_ID = "music_downloads"
    const val NOTIFICATION_ID = 1201
  }
}
