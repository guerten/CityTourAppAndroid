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
import android.media.MediaPlayer.OnCompletionListener
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



/**
 * In music player, playback of songs has to be done within a service which runs in background
 * even after the application is closed. We will create such service to handle the inputs given
 * through the buttons shown by the Notification layout
 */
class NotificationService : Service() {

    private var mp: MediaPlayer? = null

    private lateinit var touristicPlace: TouristicPlace

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d("sefs", "onDestroy")
        removeAudio()
        super.onDestroy()
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
        if (intent == null) {
            removeAudio()
        }
        else {
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
                else ->
                    removeAudio()

            }
        }
        return START_STICKY
    }


    private fun initAudio(intent: Intent) {
        touristicPlace = intent.getSerializableExtra("touristicPlace") as TouristicPlace
        if (touristicPlaceExists()) {
            if (mp != null) {
                if (mp!!.isPlaying) {
                    mp!!.stop()
                    mp!!.reset()
                    mp!!.release()
                    mp = null
                }
            }
            mp = MediaPlayer()
            mp!!.setOnCompletionListener(OnCompletionListener {
                /*Tambien se podría poner el changePlay...*/
                removeAudio()
            })
            val myUrl = touristicPlace.audioGuideUrl

            // TODO : Por ahora si estuviera sonando un audio y sale que te acercas a otro lado, entonces directamente se va a cambiar a este otro audio a sonar sin avisar ni nada!
            mp!!.apply {
                setDataSource(myUrl)
                prepare()
                start()
            }
        }
    }


    private fun playAudio() {
        if (touristicPlaceExists()) {
            if (mp == null) {
                mp = MediaPlayer()
                val myUrl = touristicPlace.audioGuideUrl
                mp!!.apply {
                    setDataSource(myUrl)
                    prepare()
                    start()
                }
            } else {
                if (mp!!.isPlaying) {
                    changePauseToPlayButton()
                    mp!!.pause()
                } else {
                    changePlayToPauseButton()
                    mp!!.start()
                }
            }
        }
    }

    private fun removeAudio() {
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

    private fun changePlayToPauseButton () {
        NotificationGenerator.managerInstance.notification.contentView.setImageViewResource(R.id.status_bar_play,R.drawable.ic_action_pause)
        NotificationGenerator.managerInstance.notificationManager?.notify(NOTIFICATION_ID_BIG_CONTENT, NotificationGenerator.managerInstance.notification)
    }

    private fun changePauseToPlayButton () {
        NotificationGenerator.managerInstance.notification.contentView.setImageViewResource(R.id.status_bar_play,R.drawable.ic_action_play)
        NotificationGenerator.managerInstance.notificationManager?.notify(NOTIFICATION_ID_BIG_CONTENT, NotificationGenerator.managerInstance.notification)
    }
    private fun touristicPlaceExists () : Boolean {
        return touristicPlace!=null
    }
}