package com.android4dev.CityTourApp

import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v4.app.FragmentActivity

class SettingsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, MainSettingsFragment()).commit()
    }

     class MainSettingsFragment : PreferenceFragment() {
         override fun onCreate(savedInstanceState: Bundle?) {
             super.onCreate(savedInstanceState)
             addPreferencesFromResource(R.xml.preferences)
         }
     }
}
