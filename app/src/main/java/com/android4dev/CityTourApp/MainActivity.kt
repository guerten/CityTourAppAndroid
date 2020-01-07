package com.android4dev.CityTourApp

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import com.android4dev.CityTourApp.models.TouristicPlace
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.DividerItemDecoration
import android.widget.Toast
import com.android4dev.CityTourApp.data.DataInitializer
import kotlinx.android.synthetic.main.fragment_map.*
import kotlin.collections.ArrayList
import com.android4dev.CityTourApp.models.TP_Type
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_map.*

class MainActivity : AppCompatActivity() , OnMapReadyCallback {

    var currentLocation: LatLng = LatLng(42.0, 2.0)
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var mMap: GoogleMap
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val touristicPlacesList: ArrayList<TouristicPlace> = DataInitializer().getTouristicPlaces()
    private var lastPositionMarker: Marker? = null

    companion object {
        var instance: MainActivity? = null

        fun getMainInstance(): MainActivity {
            return instance!!
        }
    }

    private fun startLocationService() {
        val intent = Intent(this, MyBackgroundLocationService::class.java)
        startService(intent)
    }

    private fun stopLocationService(view: View) {
        val intent = Intent(this, MyBackgroundLocationService::class.java)
        stopService(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initBottomSheetView()

        instance = this

        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        updateLocation()
                        startLocationService()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {}

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Toast.makeText(this@MainActivity, "You must accept this permission", Toast.LENGTH_LONG).show()
                    }

                }).check()
    }

    private fun updateLocation() {
        buildLocationRequest()

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this@MainActivity, LocationService::class.java)
        intent.action = LocationService.ACTION_PROCESS_UPDATE
        return PendingIntent.getBroadcast(this@MainActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun updatePosition(location: Location) {
        //Log.e("is location", "drawing new position from receiver")

        /*this@MainActivity.runOnUiThread {
            lastPositionMarker?.remove()
            val newPosition = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions().position(newPosition).title("You are here!!")
            lastPositionMarker = mMap.addMarker(markerOptions)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 2000.0F))
        }*/

        /***
         * Here we can check if a touristic place is discovered when app is running in foreground
         */
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onResume() {
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out)
        super.onResume()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true

        /*mMap.uiSettings.isScrollGesturesEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = true*/

        for (tp in touristicPlacesList) {
            val icon: BitmapDescriptor = when (tp.type) {
                TP_Type.HISTORIC -> BitmapDescriptorFactory.fromResource(R.drawable.historic)

                TP_Type.NATURE -> BitmapDescriptorFactory.fromResource(R.drawable.tree)
            }
            val markerOptions = MarkerOptions().position(LatLng(tp.coordinates.latitude, tp.coordinates.longitude)).icon(icon)
            mMap.addMarker(markerOptions)

        }

        settingsButton.setOnClickListener {
            // Open new view with settings
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initBottomSheetView() {

        bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(bottomSheet)

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
