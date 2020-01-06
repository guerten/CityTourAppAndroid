package com.android4dev.CityTourApp

import android.R
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.android4dev.CityTourApp.models.TouristicPlace
import com.google.android.gms.location.*


class MyBackgroundLocationService : Service() {

    private val TAG = "BACKGROUND LOC SERVICE"
    private var mLocationClient: FusedLocationProviderClient? = null
    private val closeTouristicPlaces: ArrayList<TouristicPlace> = ArrayList()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            Log.e(TAG, "on location result $locationResult")
            if (locationResult == null) {
                return
            }

            val location = locationResult.locations.last()

        }
    }

    override fun onCreate() {
        Log.e(TAG, "on create")
        super.onCreate()
        mLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "on start commmand")
        getLocationUpdates()
        return START_STICKY
    }

    private fun getLocationUpdates() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 4000
        locationRequest.maxWaitTime = 15*1000

        if( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        Log.e(TAG, "On destroy")
        mLocationClient?.removeLocationUpdates(locationCallback)
    }
}
