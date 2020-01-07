package com.android4dev.CityTourApp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.view.View
import android.widget.RemoteViews
import com.android4dev.CityTourApp.models.TouristicPlace
import android.content.IntentFilter



const val NOTIFY_DELETE = "com.android4dev.CityTourApp.delete"
const val NOTIFY_INIT = "com.android4dev.CityTourApp.init"
const val NOTIFY_PLAY = "com.android4dev.CityTourApp.play"
const val NOTIFICATION_ID_BIG_CONTENT = 99

class NotificationGenerator {

    var notificationManager: NotificationManager? = null
    private var notificationChannel: NotificationChannel? = null
    private val channelId = "com.android4dev.CityTourApp"
    private val description = "Test notification"
    private lateinit var touristicPlace: TouristicPlace
    lateinit var context: Context
    lateinit var smallView: RemoteViews

    lateinit var notification: Notification

    companion object {
        private var instance = NotificationGenerator ()

        val managerInstance: NotificationGenerator
            get() {
                return instance!!
            }
    }

    /**
     * Creates a Custom Notifications that is usually used by music player apps.
     * @param [context] application context for associate the notification with.
     * @see [http://www.tutorialsface.com/2015/08/android-custom-notification-tutorial/]
     */
    fun showBigContentMusicPlayer(contextAux: Context,touristicPlaceAux: TouristicPlace) {
        instance = this
        context = contextAux
        touristicPlace = touristicPlaceAux

        // Using RemoteViews to bind custom layouts into Notification
        smallView = RemoteViews(context.packageName, R.layout.status_bar)

        // showing default album image
        smallView.setViewVisibility(R.id.status_bar_icon, View.VISIBLE)
        smallView.setViewVisibility(R.id.status_bar_album_art, View.GONE)

        setListeners(smallView, context)

        // Build the content of the notification
        val nBuilder = getNotificationBuilder(context,
                "Music Player",
                "Control Audio",
                R.drawable.laseo,
                "Illustrate how a big content notification can be created.")

        // Notification through notification manager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nBuilder.setCustomContentView(smallView)
            notification = nBuilder.build()
        } else {
            notification = nBuilder.build()
            notification.contentView = smallView
        }

        // Notification through notification manager
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE

        notificationManager?.notify(NOTIFICATION_ID_BIG_CONTENT, notification)
    }


    private fun setListeners(smallView: RemoteViews, context: Context) {
        val intentDelete = Intent(context, NotificationService::class.java)
        intentDelete.action = NOTIFY_DELETE
        val pendingIntentDelete = PendingIntent.getService(context, 0, intentDelete, PendingIntent.FLAG_UPDATE_CURRENT)
        smallView.setOnClickPendingIntent(R.id.status_bar_collapse, pendingIntentDelete)
        val intentPlay = Intent(context, NotificationService::class.java)
        intentPlay.action = NOTIFY_PLAY
        val pendingIntentPlay = PendingIntent.getService(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)
        smallView.setOnClickPendingIntent(R.id.status_bar_play, pendingIntentPlay)
        smallView.setTextViewText(R.id.status_bar_track_name, touristicPlace.title)
        smallView.setTextViewText(R.id.status_bar_artist_name, touristicPlace.subtitle)
    }

    private fun getNotificationBuilder(context: Context,
                                       notificationTitle: String,
                                       notificationText: String,
                                       notificationIconId: Int,
                                       notificationTicker: String): Notification.Builder {
        // Define the notification channel for newest Android versions
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = getPendingIntent(context)
        lateinit var builder: Notification.Builder

        if (Build.VERSION.SDK_INT >= O) {
            if (null == notificationChannel) {
                notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel?.enableLights(true)
                notificationChannel?.lightColor = Color.GREEN
                notificationChannel?.enableVibration(false)
                notificationManager?.createNotificationChannel(notificationChannel)
            }
            builder = Notification.Builder(context, channelId)
        } else {
            builder = Notification.Builder(context)
        }
        var intentDelete = Intent(context, NotificationService::class.java)
        intentDelete.action = NOTIFY_DELETE
        val pendingIntentDelete = PendingIntent.getService(context, 0, intentDelete, PendingIntent.FLAG_UPDATE_CURRENT)

        // Build the content of the notification
        builder.setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setSmallIcon(notificationIconId)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, notificationIconId))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(notificationTicker)
                .setDeleteIntent(pendingIntentDelete)
        // Restricts the notification information when the screen is blocked.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PRIVATE)
        }

        return builder
    }

    private fun getPendingIntent(context: Context): PendingIntent {

        var notificationIntentClass: Class<*> = TouristicPlaceDetail::class.java
        val resultIntent = Intent(context, notificationIntentClass)
        resultIntent.putExtra("tpItem",touristicPlace)

        // The following flags are used so when we go back from the new/clear activity we don't get back to the first activity or something like that
/*
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
*/
        val resultPendingIntent = PendingIntent.getActivity(context, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return resultPendingIntent
    }
}