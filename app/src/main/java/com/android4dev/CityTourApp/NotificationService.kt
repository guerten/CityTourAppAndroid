package com.android4dev.CityTourApp

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.ImageButton
import android.widget.RemoteViews
import android.widget.Toast
import com.android4dev.CityTourApp.models.TouristicPlace
import java.io.FileInputStream

/**
 * In music player, playback of songs has to be done within a service which runs in background
 * even after the application is closed. We will create such service to handle the inputs given
 * through the buttons shown by the Notification layout
 */
class NotificationService : Service() {

    var mp: MediaPlayer? = null

    private lateinit var touristicPlace: TouristicPlace

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * Receive the big content actions in background.
     * @param [intent] The Intent supplied to startService, as given.
     *        This may be null if the service is being restarted after its process has gone away,
     *        and it had previously returned anything except START_STICKY_COMPATIBILITY.
     * @param [flags] Additional data about this start request.
     * @param [startId] A unique integer representing this specific request to start.     *
     * @return The return value indicates what semantics the system should use for the service's
     *         current started state.  It may be one of the constants associated with the
     *         START_CONTINUATION_MASK bits.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("sef", intent!!.action.toString())

        val action = intent!!.action
        when (action) {
            NOTIFY_PLAY -> {
                playAudio()
            }
            NOTIFY_DELETE -> {
                removeAudio()
            }
            NOTIFY_INIT -> {
                initAudio(intent)
            }
        }
        return START_STICKY
    }


    fun initAudio(intent: Intent) {
/*        if (::touristicPlace.isInitialized){
            Log.d("touristicPlace:::", touristicPlace.toString())
        }*/

        touristicPlace = intent.getSerializableExtra("touristicPlace") as TouristicPlace
        Log.d("touristicPlace", touristicPlace.title)

        if (mp != null) {
            if (mp!!.isPlaying) {
                mp!!.stop()
                mp!!.reset()
                mp!!.release()
                mp = null
            }

        }
        mp = MediaPlayer()
        val myUrl = "https://www.audioguias-bluehertz.es/audioguia_zaragoza/ingles/en_22_aljaferia.mp3"
        mp!!.apply {
            setDataSource(myUrl)
            prepare()
            start()
        }


        Toast.makeText(applicationContext, "Handle the PLAY button", Toast.LENGTH_LONG).show()
    }


    fun playAudio() {

        Log.d("osiejf", mp!!.isPlaying.toString())

        if (mp == null) {
            mp = MediaPlayer()
            val myUrl = "https://www.audioguias-bluehertz.es/audioguia_zaragoza/ingles/en_22_aljaferia.mp3"
            mp!!.apply {
                setDataSource(myUrl)
                prepare()
                start()
            }
        } else {
            if (mp!!.isPlaying) {
                NotificationGenerator.managerInstance.notification.contentView.setImageViewResource(R.id.status_bar_play,R.drawable.ic_action_pause)
/*
                NotificationGenerator.managerInstance.notification.contentView.setImageViewResource(R.id.status_bar_play_expanded,R.drawable.ic_action_pause)
*/
                NotificationGenerator.managerInstance.notificationManager?.notify(NOTIFICATION_ID_BIG_CONTENT, NotificationGenerator.managerInstance.notification)

                mp!!.pause()
            } else {
                NotificationGenerator.managerInstance.notification.contentView.setImageViewResource(R.id.status_bar_play,R.drawable.ic_action_play)
/*
                NotificationGenerator.managerInstance.notification.contentView.setImageViewResource(R.id.status_bar_play_expanded,R.drawable.ic_action_play)
*/
                NotificationGenerator.managerInstance.notificationManager?.notify(NOTIFICATION_ID_BIG_CONTENT, NotificationGenerator.managerInstance.notification)

                mp!!.start()
            }
        }
    }

    fun removeAudio() {

        if (mp != null) {
            if (mp!!.isPlaying) {
                mp!!.stop()
                mp!!.reset()
                mp!!.release()
                mp = null
            }
        }

        Toast.makeText(applicationContext, "Handle the DELETE button", Toast.LENGTH_LONG).show()
        stopForeground(true)
        stopSelf()
        // Terminate the notification
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID_BIG_CONTENT)
    }
}