package com.android4dev.CityTourApp

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
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
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    var currentLocation: LatLng = LatLng(42.0, 2.0)
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var mMap: GoogleMap
    private val touristicPlacesList: ArrayList<TouristicPlace> = DataInitializer().getTouristicPlaces()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        initBottomSheetView()
    }


    override fun onResume() {
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out)
        super.onResume()
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
        for ((index,touristicPlace) in touristicPlacesList.withIndex()) {
            var distanceToTouristicPlace = distance(currentLocation.latitude,currentLocation.longitude, touristicPlace.coordinates.latitude,touristicPlace.coordinates.longitude)
            touristicPlacesList[index].distance = distanceToTouristicPlace
        }
        touristicPlacesList.sortBy { it.distance }

        touristicPlacesRecyclerView.adapter = TouristicPlaceAdapter(touristicPlacesList, this)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                var mapLayoutParams = CoordinatorLayout.LayoutParams(mainActivityCoordinator.width, bottomSheet.top)
                fragmentLayoutMap.layoutParams = mapLayoutParams
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) { }
        })
    }

    private fun onLocationChanged() {
        touristicPlacesList.clear()

        for ((index,touristicPlace) in touristicPlacesList.withIndex()) {
            var distanceToTouristicPlace = distance(currentLocation.latitude,currentLocation.longitude, touristicPlace.coordinates.latitude,touristicPlace.coordinates.longitude)
            touristicPlacesList[index].distance = distanceToTouristicPlace
        }
        touristicPlacesList.sortBy { it.distance }

        touristicPlacesRecyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun distance(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): Double {
        val radius = 6378137.0   // approximate Earth radius, *in meters*
        val deltaLat = toLat - fromLat
        val deltaLon = toLon - fromLon
        val angle = (2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(deltaLat / 2), 2.0) + Math.cos(fromLat) * Math.cos(toLat) *
                        Math.pow(Math.sin(deltaLon / 2), 2.0))))
        return radius * angle
    }
}
