package com.android4dev.CityTourApp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
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

class MainActivity : AppCompatActivity() , OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var mMap: GoogleMap
    private lateinit var locationRequest: LocationRequest
    val touristicPlacesList: ArrayList<TouristicPlace> = DataInitializer().getTouristicPlaces()
    private var shouldUpdateView: Boolean = false
    private var currentKnownLocation: LatLng = Globals.DEFAULT_LOCATION
    var activityActive : Boolean = false

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
        saveCurrentLocationToPrefs()
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
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {}

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Toast.makeText(this@MainActivity, "You must accept this permission", Toast.LENGTH_LONG).show()
                    }

                }).check()
    }

    private fun startLocationService() {
        val intent = Intent(this, MyBackgroundLocationService::class.java)
        startService(intent)
    }

    private fun stopLocationService() {
        val intent = Intent(this, MyBackgroundLocationService::class.java)
        stopService(intent)
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
        mMap.setOnInfoWindowClickListener(this)
        mMap.setOnMarkerClickListener(this)
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(applicationContext, R.raw.map_style))
        mMap.isMyLocationEnabled = true

        centerMapToPosition(currentKnownLocation)

        for (tp in touristicPlacesList) {
            mMap.addMarker(MarkerOptions().title(tp.title).snippet("Distancia: ${String.format("%.1f", tp.distance)} metros").position(LatLng(tp.coordinates.latitude, tp.coordinates.longitude)).icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(tp.type))))
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun resizeBitmap(type: TP_Type): Bitmap? {

        val imageBitmap = when (type) {
            TP_Type.HISTORIC -> {
                BitmapFactory.decodeResource(resources, resources.getIdentifier("icono_cultura", "drawable", packageName))
            }
            TP_Type.NATURE -> {
                BitmapFactory.decodeResource(resources, resources.getIdentifier("icono_naturaleza", "drawable", packageName))
            }
            TP_Type.BUSINESS -> {
                BitmapFactory.decodeResource(resources, resources.getIdentifier("icono_negocios", "drawable", packageName))
            }
        }
        return Bitmap.createScaledBitmap(imageBitmap, 128, 128, false)

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

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.v("BS Change State", "$newState")
            }
        })
    }

    override fun onLocationChanged(newLocation: Location) {
        shouldUpdateView = true
        currentKnownLocation = LatLng(newLocation.latitude,newLocation.longitude)

        if (activityActive){
            centerMapToPosition(currentKnownLocation)
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

    private fun distance(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): Float {
        val loc1  = Location("")
        loc1.latitude = fromLat
        loc1.longitude = fromLon

        val loc2  = Location("")
        loc2.latitude = toLat
        loc2.longitude = toLon

        return loc1.distanceTo(loc2)
    }

    private fun centerMapToPosition(location: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 17f)
        mMap.animateCamera(cameraUpdate)
        orderTouristicPlacesList (currentKnownLocation)
    }

    /* PREFERENCES: SAVE AND GET LAST KNOWN LOCATION */

    private fun saveCurrentLocationToPrefs() {
        val gsonCurrentKnownLocation = Gson()
        val jsonCurrentKnownLocation = gsonCurrentKnownLocation.toJson(currentKnownLocation)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPref.edit()){
            putString(Globals.LAST_KNOWN_LOCATION_PREF, jsonCurrentKnownLocation)
            commit()
        }
    }

    private fun getLastLocationFromPrefs() {
        val gsonCurrentKnownLocation = Gson()

        val jsonLastKnownLocation = PreferenceManager.getDefaultSharedPreferences(this).getString(Globals.LAST_KNOWN_LOCATION_PREF, null)
        if (jsonLastKnownLocation != null) {
            currentKnownLocation = gsonCurrentKnownLocation.fromJson(jsonLastKnownLocation, LatLng::class.java)
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        return if(marker != null) {
            val distanceToTouristicPlace = distance(marker.position.latitude, marker.position.longitude, currentKnownLocation.latitude, currentKnownLocation.longitude)
            marker.snippet = "Distancia: ${String.format("%.1f", distanceToTouristicPlace)} metros"
            marker.showInfoWindow()
            true
        } else {
            false
        }
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if (marker != null) {
            val touristicPlace = touristicPlacesList.firstOrNull { it.title == marker.title }
            if (touristicPlace != null) {
                val intent = Intent(applicationContext,TouristicPlaceDetail::class.java)
                intent.putExtra("tpItem", touristicPlace)
                applicationContext.startActivity(intent)
            }
        }
    }

}
