package com.android4dev.bottomsheet.models

class Coordinates (val latitude: Float , val longitude: Float)

class PoligonArea (val array: Array<Coordinates>)


class TouristicPlace(
        val title: String,
/*        val subtitle: String,
        val mainImageUri: String,
        var coordinates: Coordinates,
        val poligonArea: PoligonArea,*/
        val description: String
)