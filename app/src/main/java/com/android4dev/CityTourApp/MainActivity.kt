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
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import android.widget.Toast
import com.android4dev.CityTourApp.models.TouristicPlace
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.DividerItemDecoration
import com.android4dev.CityTourApp.models.TP_Type
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_map.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() , OnMapReadyCallback {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var mMap: GoogleMap
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val touristicPlacesList: ArrayList<TouristicPlace> = ArrayList()
    private var lastPositionMarker: Marker? = null

    companion object {
        var instance: MainActivity? = null

        fun getMainInstance(): MainActivity {
            return instance!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addTouristicPlacesToList()

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

    fun updatePosition(location: Location) {
        Log.e("is location", "drawing new position from receiver")

        this@MainActivity.runOnUiThread {
            lastPositionMarker?.remove()
            val newPosition = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions().position(newPosition).title("You are here!!")
            lastPositionMarker = mMap.addMarker(markerOptions)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 2000.0F))
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this@MainActivity, LocationService::class.java)
        intent.action = LocationService.ACTION_PROCESS_UPDATE
        return PendingIntent.getBroadcast(this@MainActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_close_bottomSheet -> {
                openCloseBottomSheet()
                Toast.makeText(applicationContext, "open_close_bottomSheet", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.action_exit ->{
                Toast.makeText(applicationContext, "action_exit", Toast.LENGTH_LONG).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isClockwise(region: List<LatLng>) : Boolean {
        val size = region.size
        var a = region[size - 1]
        var aux = 0.0

        for (x in 0 until size) {
            val b = region[x]
            aux += (b.latitude - a.latitude) * (b.longitude + a.latitude)
            a = b
        }
        return aux <= 0
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // mMap.isMyLocationEnabled = true

        /*mMap.uiSettings.isScrollGesturesEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = true*/

        for (tp in touristicPlacesList) {
            Log.e("IS TP CLOCKWISE?", "${isClockwise(tp.poligonArea)}")
            /*val polygon = mMap.addPolygon(PolygonOptions().add(tp.poligonArea[0], tp.poligonArea[1], tp.poligonArea[2], tp.poligonArea[3])
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE))
            polygon.isVisible = false
            val polyline = mMap.addPolyline(PolylineOptions()
                    .clickable(false)
                    .add(
                            tp.poligonArea[0],
                            tp.poligonArea[1],
                            tp.poligonArea[2],
                            tp.poligonArea[3]
                    ))*/
            val icon: BitmapDescriptor = when (tp.type) {
                TP_Type.HISTORIC -> BitmapDescriptorFactory.fromResource(R.drawable.historic)

                TP_Type.NATURE -> BitmapDescriptorFactory.fromResource(R.drawable.tree)
            }
            val markerOptions = MarkerOptions().position(tp.coordinates).icon(icon)
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

        // Creates a vertical Layout Manager
        turisticPlacesList.layoutManager = LinearLayoutManager(this)

        // adding de dividir for the recycleView
        val mDividerItemDecoration = DividerItemDecoration(turisticPlacesList.context, DividerItemDecoration.VERTICAL)
        turisticPlacesList.addItemDecoration(mDividerItemDecoration)

        turisticPlacesList.adapter = TouristicPlaceAdapter(touristicPlacesList, this)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                val mapLayoutParams = CoordinatorLayout.LayoutParams(mainActivityCoordinator.width, bottomSheet.top)
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

    fun addTouristicPlacesToList() {
        touristicPlacesList.add(TouristicPlace(
                "Casa",
                "goya",
                LatLng(41.665167, -0.869308),
                listOf(LatLng(41.6650169,-0.8691469), LatLng(41.6650169,-0.8691469), LatLng(41.6650169,-0.8691469), LatLng(41.6650169,-0.8691469)),
                "Esta es mi casica",
                TP_Type.NATURE))
        touristicPlacesList.add(TouristicPlace(
                "Azucarera",
                "el_pilar",
                LatLng(41.6638817,-0.868364),
                listOf(LatLng(41.663603, -0.868355), LatLng(41.6643099,-0.8682033), LatLng(41.6643099,-0.8682033), LatLng(41.6643099,-0.8682033)),
                "Azucarera to guapa",
                TP_Type.HISTORIC))

        /*touristicPlacesList.add(TouristicPlace("Goya", "goya", "La descripción de Goya"))
        touristicPlacesList.add(TouristicPlace("La lonja", "la_lonja","La descripción de la lonjita.,.. vesas oiesjhofg sofihsoi haoiehoish faios hfis an ahsiou"))
        touristicPlacesList.add(TouristicPlace("El pilar", "el_pilar", "no description el siguiente"))
        touristicPlacesList.add(TouristicPlace("La seo","laseo", ""))
        touristicPlacesList.add(TouristicPlace("Aljafería", "aljaferia", "oisejhfgoiesahf hudas huiahas h kh sdfSAF OHDUIa 1235 32i5 hsdn"))
        touristicPlacesList.add(TouristicPlace("Catedral del salvador", "catedral_del_salvador","sioejfoise"))
        touristicPlacesList.add(TouristicPlace("2Goya", "goya", "La descripción de Goya"))
        touristicPlacesList.add(TouristicPlace("2La lonja", "la_lonja","La descripción de la lonjita.,.. vesas oiesjhofg sofihsoi haoiehoish faios hfis an ahsiou"))
        touristicPlacesList.add(TouristicPlace("2El pilar", "el_pilar", "no description el siguiente"))
        touristicPlacesList.add(TouristicPlace("2La seo","laseo", ""))
        touristicPlacesList.add(TouristicPlace("2Aljafería", "aljaferia", "oisejhfgoiesahf hudas huiahas h kh sdfSAF OHDUIa 1235 32i5 hsdn"))
        touristicPlacesList.add(TouristicPlace("2Catedral del salvador", "catedral_del_salvador","sioejfoise"))
        touristicPlacesList.add(TouristicPlace("3Goya", "goya", "La descripción de Goya"))
        touristicPlacesList.add(TouristicPlace("3La lonja", "la_lonja","La descripción de la lonjita.,.. vesas oiesjhofg sofihsoi haoiehoish faios hfis an ahsiou"))
        touristicPlacesList.add(TouristicPlace("3El pilar", "el_pilar", "no description el siguiente"))
        touristicPlacesList.add(TouristicPlace("3La seo","laseo", ""))
        touristicPlacesList.add(TouristicPlace("3Aljafería", "aljaferia", "oisejhfgoiesahf hudas huiahas h kh sdfSAF OHDUIa 1235 32i5 hsdn"))
        touristicPlacesList.add(TouristicPlace("3Catedral del salvador", "catedral_del_salvador","sioejfoise"))*/

    }


    /***
     * Manually Slide up and Slide Down
     */
    private fun openCloseBottomSheet() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
        }
    }
}
