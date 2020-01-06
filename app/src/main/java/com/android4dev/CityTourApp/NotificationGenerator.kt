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
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.view.View
import android.widget.RemoteViews

/*
const val NOTIFY_PREVIOUS = "com.android4dev.CityTourApp.previous"
*/
const val NOTIFY_DELETE = "com.android4dev.CityTourApp.delete"
const val NOTIFY_INIT = "com.android4dev.CityTourApp.init"
const val NOTIFY_PAUSE = "com.android4dev.CityTourApp.pause"
const val NOTIFY_PLAY = "com.android4dev.CityTourApp.play"
/*
const val NOTIFY_NEXT = "com.android4dev.CityTourApp.next"
*/
const val STARTFOREGROUND_ACTION = "com.android4dev.CityTourApp.startforeground"
const val NOTIFICATION_ID_BIG_CONTENT = 99

/*
const val NOTIFICATION_ID_REGULAR = 9
const val NOTIFICATION_ID_BIG_TEXT_CONTENT = 666
const val NOTIFICATION_ID_BIG_PICTURE_CONTENT = 777
const val NOTIFICATION_ID_BIG_INBOX_CONTENT = 999
*/

/**
 * Class to generate the notification to open the Activity.
 * @property [notificationIntentClass] The component class that is to be used for the notification intent.
 *           The default class, for this example, is [NotificationActivity].
 * @see [https://developer.android.com/guide/topics/ui/notifiers/notifications.html]
 * @see [http://www.vogella.com/tutorials/AndroidNotifications/article.html]
 * @see [https://www.youtube.com/watch?v=VouATjZdIWo]
 * @see [https://www.youtube.com/watch?v=3FJNOrfBQEA]
 * @see [https://www.youtube.com/watch?v=wMS-m29zH20]
 */
class NotificationGenerator(var notificationIntentClass: Class<*> = MainActivity::class.java) {

    var notificationManager: NotificationManager? = null
    private var notificationChannel: NotificationChannel? = null
    private val channelId = "com.android4dev.CityTourApp"
    private val description = "Test notification"
    lateinit var contexto: Context
    lateinit var smallView: RemoteViews
/*
    lateinit var bigView: RemoteViews
*/
    lateinit var notification: Notification

    companion object {
        private lateinit var instance: NotificationGenerator

        val managerInstance: NotificationGenerator
            get() {
                if (instance == null) {
                    instance = NotificationGenerator()
                }

                return instance
            }
    }

    /**
     * Creates a Custom Notifications that is usually used by music player apps.
     * @param [context] application context for associate the notification with.
     * @see [http://www.tutorialsface.com/2015/08/android-custom-notification-tutorial/]
     */
    fun showBigContentMusicPlayer(context: Context) {
        instance = this
        contexto = context

        // Using RemoteViews to bind custom layouts into Notification
        smallView = RemoteViews(context.packageName, R.layout.status_bar)
/*
        bigView = RemoteViews(context.packageName, R.layout.status_bar_expanded)
*/

        // showing default album image
        smallView.setViewVisibility(R.id.status_bar_icon, View.VISIBLE)
        smallView.setViewVisibility(R.id.status_bar_album_art, View.GONE)
/*
        bigView.setImageViewBitmap(R.id.status_bar_album_art, BitmapFactory.decodeResource(context.resources, R.drawable.star))
*/
        setListeners(smallView, context)

        // Build the content of the notification
        val nBuilder = getNotificationBuilder(context,
                "Music Player",
                "Control Audio",
                R.drawable.laseo,
                "Illustrate how a big content notification can be created.")

        // Notification through notification manager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
/*
            nBuilder.setCustomBigContentView(bigView)
*/
            nBuilder.setCustomContentView(smallView)
            notification = nBuilder.build()
        } else {
            notification = nBuilder.build()
            notification.contentView = smallView
/*
            notification.bigContentView = bigView
*/
        }

        // Notification through notification manager
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE
/*
        notification.flags = Notification.FLAG_ONGOING_EVENT
*/
        notificationManager?.notify(NOTIFICATION_ID_BIG_CONTENT, notification)
    }


    /**
     * Handle the control buttons.
     * @param [bigView] remote view for big content.
     * @param [smallView] remote view for regular content.
     * @param [context] application context for associate the notification with.
     */
    private fun setListeners(smallView: RemoteViews, context: Context) {
        Log.d("setListener", "en setlistener")


        val intentDelete = Intent(context, NotificationService::class.java)
        intentDelete.action = NOTIFY_DELETE
        val pendingIntentDelete = PendingIntent.getService(context, 0, intentDelete, PendingIntent.FLAG_UPDATE_CURRENT)
/*
        bigView.setOnClickPendingIntent(R.id.status_bar_collapse, pendingIntentDelete)
*/
        smallView.setOnClickPendingIntent(R.id.status_bar_collapse, pendingIntentDelete)

        val intentPlay = Intent(context, NotificationService::class.java)
        intentPlay.action = NOTIFY_PLAY
        val pendingIntentPlay = PendingIntent.getService(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)
/*
        bigView.setOnClickPendingIntent(R.id.status_bar_play_expanded, pendingIntentPlay)
*/
        smallView.setOnClickPendingIntent(R.id.status_bar_play, pendingIntentPlay)

/*
        bigView.setTextViewText(R.id.status_bar_track_name, "Song Title")
*/
        smallView.setTextViewText(R.id.status_bar_track_name, "Song Title")

/*
        bigView.setTextViewText(R.id.status_bar_artist_name, "Artist Name")
*/
        smallView.setTextViewText(R.id.status_bar_artist_name, "Artist Name")

/*
        bigView.setTextViewText(R.id.status_bar_album_name, "Album Name")
*/
        Log.d("setListener", "en setlistener")

    }

    /**
     * Initialize the notification manager and channel Id.
     * The notification builder has the basic initialization:
     *     - AutoCancel=true
     *     - LargeIcon = SmallIcon
     * @param [context] application context for associate the notification with.
     * @param [notificationTitle] notification title.
     * @param [notificationText] notification text.
     * @param [notificationIconId] notification icon id from application resource.
     * @param [notificationTicker] notification ticker text for accessibility.
     * @return the PendingIntent to be used on this notification.
     */
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

        // Build the content of the notification
        builder.setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setSmallIcon(notificationIconId)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, notificationIconId))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(notificationTicker)
        // Restricts the notification information when the screen is blocked.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PRIVATE)
        }

        return builder
    }

    /**
     * Retorna a Intent que será utilizada nesta notificação.
     * Para poder recompor a estruturas das activities, é necessário declarar o parentesco no manifesto
     * e incluir os atributos:
     *          + launchMode="singleTask"
     *          + taskAffinity=""
     *          + excludeFromRecents="true"
     * da NotificationActivity.
     * @param [context] application context for associate the notification with.
     * @return the activity associated to the notification.
     * @see [https://developer.android.com/guide/topics/ui/notifiers/notifications.html#NotificationResponse]
     */
    private fun getPendingIntent(context: Context): PendingIntent {
        val resultIntent = Intent(context, notificationIntentClass)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        val resultPendingIntent = PendingIntent.getActivity(context, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return resultPendingIntent
    }
}