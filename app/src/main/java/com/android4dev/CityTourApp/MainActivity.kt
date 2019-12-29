package com.android4dev.CityTourApp

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import android.widget.Toast
import com.android4dev.CityTourApp.models.TouristicPlace
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.DividerItemDecoration
import com.android4dev.CityTourApp.data.DataInitializer
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var currentLocation: LatLng = LatLng(42.0, 2.0)
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var mMap: GoogleMap
    val touristicPlacesList: ArrayList<TouristicPlace> = DataInitializer().getTouristicPlaces()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        initBottomSheetView()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun initBottomSheetView() {


        bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(bottomSheet)

        // Creates a vertical Layout Manager
        touristicPlacesRecyclerView.layoutManager = LinearLayoutManager(this)

        // adding de dividir for the recycleView
        var mDividerItemDecoration = DividerItemDecoration(touristicPlacesRecyclerView.context, DividerItemDecoration.VERTICAL)
        touristicPlacesRecyclerView.addItemDecoration(mDividerItemDecoration)

        Collections.sort(touristicPlacesList, SortPlaces(currentLocation))
        touristicPlacesRecyclerView.adapter = TouristicPlaceAdapter(touristicPlacesList, this)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                var mapLayoutParams = CoordinatorLayout.LayoutParams(mainActivityCoordinator.width, bottomSheet.top)
                fragmentLayoutMap.layoutParams = mapLayoutParams
/*
                fragmentLayoutMap.setPadding(0,0,0,bottomSheetMinHeight+bottomSheetShiftDown)
                Toast.makeText(this@MainActivity, mainActivityCoordinator.width.toString(), Toast.LENGTH_SHORT).show()
*/
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Toast.makeText(this@MainActivity, "STATE_COLLAPSED", Toast.LENGTH_SHORT).show()
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        Toast.makeText(this@MainActivity, "STATE_HIDDEN", Toast.LENGTH_SHORT).show()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Toast.makeText(this@MainActivity, "STATE_EXPANDED", Toast.LENGTH_SHORT).show()
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                        Toast.makeText(this@MainActivity, "STATE_DRAGGING", Toast.LENGTH_SHORT).show()
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        Toast.makeText(this@MainActivity, "STATE_SETTLING", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun onLocationChanged() {
        touristicPlacesList.clear()
        Collections.sort(touristicPlacesList, SortPlaces(currentLocation));
        touristicPlacesRecyclerView.adapter!!.notifyDataSetChanged()
    }
}


class SortPlaces(internal var currentLoc: LatLng) : Comparator<TouristicPlace> {
    override fun compare(place1: TouristicPlace, place2: TouristicPlace): Int {
        val lat1 = place1.coordinates.latitude
        val lon1 = place1.coordinates.longitude
        val lat2 = place2.coordinates.latitude
        val lon2 = place2.coordinates.longitude

        val distanceToPlace1 = distance(currentLoc.latitude, currentLoc.longitude, lat1, lon1)
        val distanceToPlace2 = distance(currentLoc.latitude, currentLoc.longitude, lat2, lon2)
        return (distanceToPlace1 - distanceToPlace2).toInt()
    }

    fun distance(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): Double {
        val radius = 6378137.0   // approximate Earth radius, *in meters*
        val deltaLat = toLat - fromLat
        val deltaLon = toLon - fromLon
        val angle = (2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(deltaLat / 2), 2.0) + Math.cos(fromLat) * Math.cos(toLat) *
                        Math.pow(Math.sin(deltaLon / 2), 2.0))))
        return radius * angle
    }
}