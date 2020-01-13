package com.android4dev.CityTourApp.data

import com.android4dev.CityTourApp.models.Coordinates
import com.android4dev.CityTourApp.models.TP_Type
import com.android4dev.CityTourApp.models.TouristicPlace

class DataInitializer {
    private val touristicPlacesList: ArrayList<TouristicPlace> = ArrayList()

    init {
        addTouristicPlacesToList()
    }

    private fun addTouristicPlacesToList() {
        touristicPlacesList.add(TouristicPlace("Goya", "subtitulo de goyita mi preciosa joya", "goya", Coordinates(41.6504686,-0.8703448),"La descripción de Goya",8.3, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/ingles/en_04_goya.mp3", TP_Type.HISTORIC))
        touristicPlacesList.add(TouristicPlace("La lonja", "subtitulo de la lonja", "la_lonja", Coordinates(41.6514686,-0.8766448),"La descripción de la lonjita.,.. vesas oiesjhofg sofihsoi haoiehoish faios hfis an ahsiou", 7.9, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/ingles/en_02_lonja.mp3", TP_Type.HISTORIC))
        touristicPlacesList.add(TouristicPlace("El pilar", "subtitulo de el pilar la pilarica maña que baila y baila sin paraaaar","el_pilar",  Coordinates(41.656771,-0.8785192),"no description el siguiente", 9.3, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/ingles/en_01_plazapilar.mp3", TP_Type.BUSINESS))
        touristicPlacesList.add(TouristicPlace("La seo","Mejor monumento 2017 por la revista mihuevo", "laseo", Coordinates(41.6515012,-0.879018),"",6.3, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/ingles/en_09_seo.mp3", TP_Type.HISTORIC))
        touristicPlacesList.add(TouristicPlace("Aljafería", "buen monumento mejor persona","aljaferia", Coordinates(41.6754686,-0.8473448),"oisejhfgoiesahf hudas huiahas h kh sdfSAF OHDUIa 1235 32i5 hsdn", 7.9, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/ingles/en_22_aljaferia.mp3", TP_Type.NATURE))
    }

    fun getTouristicPlaces(): ArrayList<TouristicPlace> {
        return touristicPlacesList
    }




}




