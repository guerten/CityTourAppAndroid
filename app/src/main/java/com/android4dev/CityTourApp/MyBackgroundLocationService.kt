package com.android4dev.CityTourApp

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


class MyBackgroundLocationService : Service() {

    private val TAG = "BACKGROUND LOC SERVICE"
    private var mLocationClient: FusedLocationProviderClient? = null
    private val closeTouristicPlaces: ArrayList<TouristicPlace> = ArrayList()
    private var currentVisitingTouristicPlace: TouristicPlace? = null

    /* Esto se podría organizar un poco mejor */
    private val AUTOPLAY_SETTING : String = "enable_autoplay"
    private val PUSH_NOTIFICATION_SETTING : String = "enable_notification"
    private val TPVISITING_PREF : String = "currentVisitingTouristicPlace"

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {

            if (locationResult == null) {
                return
            }
            val newLocation: Location = locationResult.locations.last()

            // Order touristic places given the new location
            val mainActivityAux = MainActivity.getMainInstance()
            mainActivityAux.onLocationChanged(newLocation)

            var sharedPref = getSharedPref()



            if (currentVisitingTouristicPlace == null){
                currentVisitingTouristicPlace = getCurrentVisitingTouristicPlacePref(TPVISITING_PREF, sharedPref)
            }


            /* Check whether the user has disabled push notifications */
            var pushNotificationPref = getBooleanPref(PUSH_NOTIFICATION_SETTING, sharedPref)
            if (pushNotificationPref){

                /* Check if any touristic place is nearer than x metres */
                val firstTouristicPlaceInList = mainActivityAux.touristicPlacesList[0]

                if (firstTouristicPlaceInList.distance!! <= 50.0) {

                    /* Check if the now nearest close touristicPlace is different from the last one */
                    if (firstTouristicPlaceInList.title != currentVisitingTouristicPlace!!.title) {
                        currentVisitingTouristicPlace = firstTouristicPlaceInList
                        savePref (TPVISITING_PREF, currentVisitingTouristicPlace!!, sharedPref)

                        val serviceIntent = Intent(applicationContext, NotificationService::class.java)
                        var startNotificationWithAudioPlaying = getBooleanPref(AUTOPLAY_SETTING, sharedPref)
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
        mLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getLocationUpdates()
        return START_STICKY
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

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationClient?.removeLocationUpdates(locationCallback)
    }


    /* METHODS TO GET THE VALUES OF THE SETTINGS SAVED IN SHARED PREFERENCES*/
    private fun getSharedPref () : SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun savePref(key:String, objectToSave: Any, sharedPref: SharedPreferences) {
        var gsonObject = Gson()
        var jsonObject = gsonObject.toJson(objectToSave)
        with(sharedPref.edit()){
            putString(key, jsonObject)
            commit()
        }
    }

    private fun getCurrentVisitingTouristicPlacePref(key: String, sharedPref: SharedPreferences) : TouristicPlace? {
        var gsonCurrentKnownLocation = Gson()
        var jsonTouristicPlaceAux = sharedPref.getString(key, null)
        if (jsonTouristicPlaceAux != null){
            return gsonCurrentKnownLocation.fromJson(jsonTouristicPlaceAux, TouristicPlace::class.java)
        }

        return null
    }

    private fun getBooleanPref(key: String, sharedPref: SharedPreferences) : Boolean {
        return sharedPref.getBoolean(key, true)
    }

}
