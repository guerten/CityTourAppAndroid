package com.android4dev.CityTourApp

import android.content.Intent
import android.os.Build
import android.widget.Toast
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log


class BackgroundLocationServiceRestarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("Broadcast Listened", "Service tried to stop")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, MyBackgroundLocationService::class.java))
        } else {
            context.startService(Intent(context, MyBackgroundLocationService::class.java))
        }
    }
}