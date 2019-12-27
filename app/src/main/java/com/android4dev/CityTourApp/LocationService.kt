package com.android4dev.CityTourApp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.LocationResult
import java.lang.Exception

class LocationService : BroadcastReceiver() {

    companion object {
        val ACTION_PROCESS_UPDATE = "cityTourApp.UPDATE_LOCATION"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null) {

            val action = intent!!.action

            if(action != null && action == ACTION_PROCESS_UPDATE) {

                val result = LocationResult.extractResult(intent)

                if(result != null) {

                    val location = result.lastLocation

                    try {
                        MainActivity.getMainInstance().updatePosition(location)
                    } catch(e: Exception) {
                        Toast.makeText(context, "On receive pos error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
