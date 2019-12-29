package com.android4dev.CityTourApp.models

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class PoligonArea (val array: Array<Coordinates>) : Serializable

class Coordinates (val latitude: Double, val longitude: Double) : Serializable

class TouristicPlace (
    val title: String,
    val subtitle: String,
    val imageFileName: String,
    var coordinates: Coordinates,
    var description: String,
    var score: Double
    /*
        val poligonArea: PoligonArea,
*/
) : Serializable