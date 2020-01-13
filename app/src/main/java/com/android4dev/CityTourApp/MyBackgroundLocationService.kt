package com.android4dev.CityTourApp

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import com.android4dev.CityTourApp.models.TouristicPlace
import com.google.android.gms.location.*
import com.google.gson.Gson
import android.os.Build
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat


class MyBackgroundLocationService : Service() {
    private var mLocationClient: FusedLocationProviderClient? = null
    private var currentVisitingTouristicPlace: TouristicPlace? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (locationResult == null) {
                return
            }
            val newLocation: Location = locationResult.locations.last()

            // Order touristic places given the new location
            val mainActivityAux = MainActivity.getMainInstance()
            mainActivityAux.onLocationChanged(newLocation)

            val sharedPref = getSharedPref()

            if (currentVisitingTouristicPlace == null){
                currentVisitingTouristicPlace = getCurrentVisitingTouristicPlacePref(Globals.TPVISITING_PREF, sharedPref)
            }


            /* Check whether the user has disabled push notifications */
            val pushNotificationPref = getBooleanPref(Globals.PUSH_NOTIFICATION_SETTING, sharedPref)
            if (pushNotificationPref){
                /* Check if any touristic place is nearer than x metres */
                val firstTouristicPlaceInList = mainActivityAux.touristicPlacesList[0]

                if (firstTouristicPlaceInList.distance!! <= 50.0) {
                    /* Check if the now nearest close touristicPlace is different from the last one */
                    if (currentVisitingTouristicPlace == null || firstTouristicPlaceInList.title != currentVisitingTouristicPlace!!.title) {
                        currentVisitingTouristicPlace = firstTouristicPlaceInList
                        savePref (Globals.TPVISITING_PREF, currentVisitingTouristicPlace!!, sharedPref)

                        val serviceIntent = Intent(applicationContext, NotificationService::class.java)
                        serviceIntent.putExtra("touristicPlace", currentVisitingTouristicPlace)

                        val startNotificationWithAudioPlaying = getBooleanPref(Globals.AUTOPLAY_SETTING, sharedPref)
                        if (startNotificationWithAudioPlaying){
                            serviceIntent.action = NOTIFY_INIT
                        }
                        else {
                            serviceIntent.action = NOTIFY_INIT_PAUSED
                        }
                        startService(serviceIntent)
                        NotificationGenerator.managerInstance.showBigContentMusicPlayer(applicationContext, currentVisitingTouristicPlace!!)
                    }
                }

                else {
                    currentVisitingTouristicPlace = null
                }
            }

        }
    }


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground()
        else
            startForeground(1, Notification())
        mLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, BackgroundLocationServiceRestarter::class.java!!)
        this.sendBroadcast(broadcastIntent)
        mLocationClient?.removeLocationUpdates(locationCallback)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = Globals.channelId
        val channelName = "Background Service"
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        startForeground(2, notification)
    }

    private fun getLocationUpdates() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 4000
        locationRequest.maxWaitTime = 15 * 1000

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf()
            return
        }

        mLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    /* METHODS TO GET THE VALUES OF THE SETTINGS SAVED IN SHARED PREFERENCES*/
    private fun getSharedPref () : SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun savePref(key: String, objectToSave: Any, sharedPref: SharedPreferences) {
        val gsonObject = Gson()
        val jsonObject = gsonObject.toJson(objectToSave)
        with(sharedPref.edit()){
            putString(key, jsonObject)
            commit()
        }
    }

    private fun getCurrentVisitingTouristicPlacePref(key: String, sharedPref: SharedPreferences) : TouristicPlace? {
        val gsonCurrentKnownLocation = Gson()
        val jsonTouristicPlaceAux = sharedPref.getString(key, null)
        if (jsonTouristicPlaceAux != null){
            return gsonCurrentKnownLocation.fromJson(jsonTouristicPlaceAux, TouristicPlace::class.java)
        }
        return null
    }

    private fun getBooleanPref(key: String, sharedPref: SharedPreferences) : Boolean {
        return sharedPref.getBoolean(key, true)
    }

}
