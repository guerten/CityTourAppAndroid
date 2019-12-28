package com.android4dev.CityTourApp.models

import com.google.android.gms.maps.model.LatLng

class Coordinates (val latitude: Float , val longitude: Float)

class PoligonArea (val array: Array<Coordinates>)

enum class TP_Type { NATURE, HISTORIC }

class TouristicPlace(
        val title: String,
        /*val subtitle: String,*/
        val imageFileName: String,
        var coordinates: LatLng,
        val poligonArea: List<LatLng>,
        val description: String,
        val type: TP_Type
)