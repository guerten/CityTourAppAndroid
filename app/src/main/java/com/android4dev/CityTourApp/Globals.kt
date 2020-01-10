package com.android4dev.CityTourApp

import com.google.android.gms.maps.model.LatLng

object Globals {

    val DEFAULT_LOCATION: LatLng = LatLng(40.416775,-3.703790)

    // PREFERENCES
    const val LAST_KNOWN_LOCATION_PREF : String = "last_known_location_pref"
    const val AUTOPLAY_SETTING : String = "enable_autoplay"
    const val PUSH_NOTIFICATION_SETTING : String = "enable_notification"
    const val TPVISITING_PREF : String = "currentVisitingTouristicPlace"


    // For Notifications
    const val channelId = "com.android4dev.CityTourApp"

}