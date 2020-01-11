package com.android4dev.CityTourApp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.view.View
import android.widget.RemoteViews
import com.android4dev.CityTourApp.models.TouristicPlace
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth


const val NOTIFY_DELETE = Globals.TPVISITING_PREF + ".delete"
const val NOTIFY_INIT = Globals.TPVISITING_PREF + ".init"
const val NOTIFY_INIT_PAUSED = Globals.TPVISITING_PREF + ".initPaused"
const val NOTIFY_PLAY = Globals.TPVISITING_PREF + ".play"
const val NOTIFICATION_ID = 99

class NotificationGenerator {

    var notificationManager: NotificationManager? = null
    private var notificationChannel: NotificationChannel? = null
    private val description = "Test notification"
    private lateinit var touristicPlace: TouristicPlace
    lateinit var context: Context
    lateinit var smallView: RemoteViews

    lateinit var notification: Notification

    companion object {
        private var instance = NotificationGenerator ()

        val managerInstance: NotificationGenerator
            get() {
                return instance
            }
    }

    /**
     * Creates the audioGuideNotification with controls to play/pause and
     * delete the own notification.
     */
    fun showBigContentMusicPlayer(contextAux: Context,touristicPlaceAux: TouristicPlace) {
        instance = this
        context = contextAux
        touristicPlace = touristicPlaceAux

        // Using RemoteViews to bind custom layouts into Notification
        smallView = RemoteViews(context.packageName, R.layout.status_bar)

        // showing default album image
        smallView.setViewVisibility(R.id.status_bar_icon, View.VISIBLE)

        setListeners(smallView, context)

        // Build the content of the notification
        val nBuilder = getNotificationBuilder(context,
                "AudioGuide Player",
                "Control Audio",
                R.drawable.apollo_holo_dark_play,
                "Created a new control notification for the audioguide.")

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

        notificationManager?.notify(NOTIFICATION_ID, notification)
    }


    private fun setListeners(smallView: RemoteViews, context: Context) {

        // Delete notification intent
        val intentDelete = Intent(context, NotificationService::class.java)
        intentDelete.action = NOTIFY_DELETE
        val pendingIntentDelete = PendingIntent.getService(context, 0, intentDelete, PendingIntent.FLAG_UPDATE_CURRENT)
        smallView.setOnClickPendingIntent(R.id.status_bar_collapse, pendingIntentDelete)

        // Play/Pause notification intent
        val intentPlay = Intent(context, NotificationService::class.java)
        intentPlay.action = NOTIFY_PLAY
        val pendingIntentPlay = PendingIntent.getService(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)
        smallView.setOnClickPendingIntent(R.id.status_bar_play, pendingIntentPlay)

        var imageId = context.resources.getIdentifier(touristicPlace.imageFileName, "drawable", context.packageName)
        var imageBitmap = BitmapFactory.decodeResource(context.resources, imageId)
        var imageCircleBitmap = StylesManager.getCircleBitmap(imageBitmap)
        smallView.setImageViewBitmap(R.id.status_bar_icon, imageCircleBitmap)

        smallView.setTextViewText(R.id.status_bar_place_discovered, touristicPlace.title + " discovered")
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
                notificationChannel = NotificationChannel(Globals.channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel?.enableLights(true)
                notificationChannel?.lightColor = Color.GREEN
                notificationChannel?.enableVibration(false)
                notificationManager?.createNotificationChannel(notificationChannel)
            }
            builder = Notification.Builder(context, Globals.channelId)
        } else {
            builder = Notification.Builder(context)
        }

        // Swipe right notification behavior
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
        val resultPendingIntent = PendingIntent.getActivity(context, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return resultPendingIntent
    }
}