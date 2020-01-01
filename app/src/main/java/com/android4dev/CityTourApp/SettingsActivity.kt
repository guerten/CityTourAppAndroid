package com.android4dev.CityTourApp

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.support.v4.app.FragmentActivity
import android.util.Log


class SettingsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction().replace(android.R.id.content, MainSettingsFragment()).commit()
/*
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val info = StringBuilder()

        info.append("Filtros : ${sharedPreferences.getString("places_types","")}")
        info.append("\nNotifications: ${sharedPreferences.getString("enable_notification","")}")
        info.append("\nAutoplay : ${sharedPreferences.getString("enable_autoplay","")}")
*/

    }

     class MainSettingsFragment : PreferenceFragment() {
         override fun onCreate(savedInstanceState: Bundle?) {
             super.onCreate(savedInstanceState)
             addPreferencesFromResource(R.xml.preferences)

         }
     }
}
