package com.android4dev.CityTourApp.models

import java.io.Serializable

class Coordinates (val latitude: Double, val longitude: Double) : Serializable
enum class TP_Type { NATURE, HISTORIC, BUSINESS }

class TouristicPlace (
    val title: String,
    val subtitle: String,
    val imageFileName: String,
    var coordinates: Coordinates,
    var description: String,
    var score: Double,
    var audioGuideUrl: String,
    val type: TP_Type,
    var distance: Float? = null
) : Serializable, Cloneable {
    override public fun clone(): Any {
        return super.clone()
    }
}
