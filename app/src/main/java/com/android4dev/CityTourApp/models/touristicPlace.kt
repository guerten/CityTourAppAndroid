package com.android4dev.CityTourApp.models

import com.google.android.gms.maps.model.LatLng

class Coordinates (val latitude: Float , val longitude: Float)

enum class TP_Type { NATURE, HISTORIC }

class TouristicPlace(
        val title: String,
        /*val subtitle: String,*/
        val imageFileName: String,
        var coordinates: LatLng,
        val discoverCoordinates: LatLng,
        val description: String,
        val type: TP_Type
)