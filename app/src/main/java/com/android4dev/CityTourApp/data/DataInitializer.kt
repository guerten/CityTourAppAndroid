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
        touristicPlacesList.add(TouristicPlace("El pilar", "subtitulo de el pilar la pilarica maña que baila y baila sin paraaaar","el_pilar",  Coordinates(41.6562918,-0.8804382),"no description el siguiente", 9.3, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/ingles/en_01_plazapilar.mp3", TP_Type.HISTORIC))
        touristicPlacesList.add(TouristicPlace("La seo","Mejor monumento 2017 por la revista mihuevo", "laseo", Coordinates(41.6554686,-0.8773448),"",6.3, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/ingles/en_09_seo.mp3", TP_Type.HISTORIC))
        touristicPlacesList.add(TouristicPlace("Aljafería", "buen monumento mejor persona","aljaferia", Coordinates(41.6754686,-0.8473448),"oisejhfgoiesahf hudas huiahas h kh sdfSAF OHDUIa 1235 32i5 hsdn", 7.9, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/ingles/en_22_aljaferia.mp3", TP_Type.NATURE))
/*
        touristicPlacesList.add(TouristicPlace("Catedral del salvador", "No la conoce ni su padre",  "catedral_del_salvador", Coordinates(41.6574686,-0.8773448),"sioejfoise", 3.5))
*/
/*
        touristicPlacesList.add(TouristicPlace("Grancasa", "Esto si es lo mejor del puto mundo... y alladito de casa", "grancasa", Coordinates(41.6697002,-0.891558),"La descripción de Grancasita weey", 9.9))
*/
        touristicPlacesList.add(TouristicPlace("Goya2", "subtitulo de goyita mi preciosa joya","goya", Coordinates(41.6504685,-0.8703448),"La descripción de Goya", 9.2, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/esp/goya.mp3", TP_Type.NATURE))
        touristicPlacesList.add(TouristicPlace("La lonja2", "subtitulo de la lonja","la_lonja", Coordinates(41.6514685,-0.8766448),"La descripción de la lonjita.,.. vesas oiesjhofg sofihsoi haoiehoish faios hfis an ahsiou", 8.3, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/esp/lonja_de_mercaderes.mp3", TP_Type.NATURE))
        touristicPlacesList.add(TouristicPlace("El pilar2","subtitulo de el pilar la pilarica maña que baila y baila sin paraaaar" ,"el_pilar",  Coordinates(41.6562917,-0.8804382),"no description el siguiente", 6.6, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/esp/plaza_del_pilar.mp3", TP_Type.NATURE))
        touristicPlacesList.add(TouristicPlace("La seo2","Mejor monumento 2017 por la revista mihuevo","laseo", Coordinates(41.6554685,-0.8773448),"", 9.2, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/esp/seo.mp3", TP_Type.NATURE))
        touristicPlacesList.add(TouristicPlace("Aljafería2", "aljafería mañaaa", "aljaferia", Coordinates(41.6754685,-0.8473448),"oisejhfgoiesahf hudas huiahas h kh sdfSAF OHDUIa 1235 32i5 hsdn", 8.3, "https://www.audioguias-bluehertz.es/audioguia_zaragoza/esp/aljaferia.mp3", TP_Type.NATURE))
/*
        touristicPlacesList.add(TouristicPlace("Catedral del salvador2", "Lo dicho ni su padre","catedral_del_salvador", Coordinates(41.6574685,-0.8773448),"sioejfoise", 9.0))
*/
/*
        touristicPlacesList.add(TouristicPlace("Grancasa2", "YYYYY suuuu", "grancasa", Coordinates(41.6697001,-0.891558),"La descripción de Grancasita weey", 9.7))
*/
    }

    fun getTouristicPlaces(): ArrayList<TouristicPlace> {
        return touristicPlacesList
    }




}




