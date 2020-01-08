package com.android4dev.CityTourApp

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import com.android4dev.CityTourApp.models.TouristicPlace
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.widget.Toast
import com.android4dev.CityTourApp.data.DataInitializer
import kotlinx.android.synthetic.main.fragment_map.*
import kotlin.collections.ArrayList
import com.android4dev.CityTourApp.models.TP_Type
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MainActivity : AppCompatActivity() , OnMapReadyCallback, LocationListener {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var mMap: GoogleMap
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    val touristicPlacesList: ArrayList<TouristicPlace> = DataInitializer().getTouristicPlaces()
    private var lastPositionMarker: Marker? = null
    private var shouldUpdateView: Boolean = true
    private var currentKnownLocation: LatLng = LatLng(40.416775,-3.703790)
    var activityActive : Boolean = false
    var LAST_KNOWN_LOCATION_PREF : String = "last_known_location_pref"

    companion object {
        var instance: MainActivity = MainActivity()

        fun getMainInstance(): MainActivity {
            return instance
        }
    }

    override fun onStart() {
        activityActive = true
        super.onStart()
    }

    override fun onResume() {

        /* If a new location has come, then we need to update the recycleView UI*/
        if (shouldUpdateView){
            touristicPlacesRecyclerView.adapter!!.notifyDataSetChanged()
            shouldUpdateView = false
        }
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out)
        super.onResume()
    }

    override fun onStop() {
        activityActive = false
        saveCurrentLocationToPrefs ()
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getLastLocationFromPrefs()

        instance = this

        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        val mapFragment = supportFragmentManager
                                .findFragmentById(R.id.map) as SupportMapFragment
                        mapFragment.getMapAsync(instance)
                        initBottomSheetView()
                        startLocationService()
                        updateLocation()
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

    private fun startLocationService() {
        val intent = Intent(this, MyBackgroundLocationService::class.java)
        startService(intent)
    }

    private fun stopLocationService() {
        val intent = Intent(this, MyBackgroundLocationService::class.java)
        stopService(intent)
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

        val mDividerItemDecoration = DividerItemDecoration(touristicPlacesRecyclerView.context, DividerItemDecoration.VERTICAL)
        touristicPlacesRecyclerView.addItemDecoration(mDividerItemDecoration)

        orderTouristicPlacesList(currentKnownLocation)

        touristicPlacesRecyclerView.adapter = TouristicPlaceAdapter(touristicPlacesList, this)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val mapLayoutParams = CoordinatorLayout.LayoutParams(mainActivityCoordinator.width, bottomSheet.top)
                fragmentLayoutMap.layoutParams = mapLayoutParams
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) { }
        })
    }

    override fun onLocationChanged(newLocation: Location) {
        shouldUpdateView = true
        currentKnownLocation = LatLng(newLocation.latitude,newLocation.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentKnownLocation, 18f)
        mMap.animateCamera(cameraUpdate)
        orderTouristicPlacesList (currentKnownLocation)
        if (activityActive){
            touristicPlacesRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.v("Loc Update", "\nProvider status changed: $provider, status=$status, extras=$extras");

    }

    override fun onProviderEnabled(provider: String?) {
        Log.v("Loc Update", "\nProvider enabled: $provider");
    }

    override fun onProviderDisabled(provider: String?) {
        Log.v("Loc Update", "\nProvider disabled: $provider");
    }


    private fun orderTouristicPlacesList(newLocation: LatLng) {
        for ((index,touristicPlace) in touristicPlacesList.withIndex()) {
            val distanceToTouristicPlace = distance(newLocation.latitude,newLocation.longitude, touristicPlace.coordinates.latitude,touristicPlace.coordinates.longitude)
            touristicPlacesList[index].distance = distanceToTouristicPlace
        }
        touristicPlacesList.sortBy { it.distance }
    }

    /* Obtain the distance between 2 given points by their latitude and longitude*/
    private fun distance(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): Float {
        val loc1  = Location("")
        loc1.latitude = fromLat
        loc1.longitude = fromLon

        val loc2  = Location("")
        loc2.latitude = toLat
        loc2.longitude = toLon

        return loc1.distanceTo(loc2)
    }




    /* PREFERENCES: SAVE AND GET LAST KNOWN LOCATION */

    private fun saveCurrentLocationToPrefs() {
        val gsonCurrentKnownLocation = Gson()
        val jsonCurrentKnownLocation = gsonCurrentKnownLocation.toJson(currentKnownLocation)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPref.edit()){
            putString(LAST_KNOWN_LOCATION_PREF, jsonCurrentKnownLocation)
            commit()
        }
    }

    private fun getLastLocationFromPrefs() {
        val gsonCurrentKnownLocation = Gson()

        val jsonLastKnownLocation = PreferenceManager.getDefaultSharedPreferences(this).getString(LAST_KNOWN_LOCATION_PREF, null)
        if (jsonLastKnownLocation != null) {
            currentKnownLocation = gsonCurrentKnownLocation.fromJson(jsonLastKnownLocation, LatLng::class.java)
        }
    }
}
