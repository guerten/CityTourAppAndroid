package com.android4dev.CityTourApp

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.android4dev.CityTourApp.models.TouristicPlace


/**
 * Play an audioguide within a service which runs in background
 * In this case, when the user kill the app we are also gonna kill the service
 * We will create such service to handle the inputs given
 * through the buttons shown by the Notification layout
 */
class NotificationService : Service() {

    private var mp: MediaPlayer? = null

    private lateinit var touristicPlace: TouristicPlace

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        removeAudio()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            removeAudio()
        }
        else {
            when (intent.action) {
                NOTIFY_PLAY -> {
                    playAudio()
                }
                NOTIFY_DELETE -> {
                    removeAudio()
                }
                NOTIFY_INIT -> {
                    initAudio(intent, true)
                }
                NOTIFY_INIT_PAUSED -> {
                    initAudio(intent, false)
                }
                else ->
                    removeAudio()
            }
        }
        return START_STICKY
    }


    private fun initAudio(intent: Intent, playingAudioGuide: Boolean) {
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
            mp!!.setOnCompletionListener {
                removeAudio()
            }
            val myUrl = touristicPlace.audioGuideUrl

            mp!!.apply {
                setDataSource(myUrl)
                prepare()
                if (playingAudioGuide){
                    start()
                }
                else {
                    changePauseToPlayButton()
                }
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
        stopForeground(true)
        stopSelf()
        // Terminate the notification
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun changePlayToPauseButton () {
        NotificationGenerator.managerInstance.notification.contentView.setImageViewResource(R.id.status_bar_play,R.drawable.ic_action_pause_black)
        NotificationGenerator.managerInstance.notificationManager?.notify(NOTIFICATION_ID, NotificationGenerator.managerInstance.notification)
    }

    private fun changePauseToPlayButton () {
        NotificationGenerator.managerInstance.notification.contentView.setImageViewResource(R.id.status_bar_play,R.drawable.ic_action_play_black)
        NotificationGenerator.managerInstance.notificationManager?.notify(NOTIFICATION_ID, NotificationGenerator.managerInstance.notification)
    }
    private fun touristicPlaceExists () : Boolean {
        return ::touristicPlace.isInitialized
    }
}