package com.android4dev.CityTourApp

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_map.*


class MainActivity : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var mMap: GoogleMap
    val touristicPlacesList: ArrayList<TouristicPlace> = ArrayList()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d("sjoeifj", "soiefjoisef")
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
/*
        setSupportActionBar(toolbar)
*/
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

        addTouristicPlacesToList()

        // Creates a vertical Layout Manager
        turisticPlacesList.layoutManager = LinearLayoutManager(this)

        // adding de dividir for the recycleView
        var mDividerItemDecoration = DividerItemDecoration(turisticPlacesList.context, DividerItemDecoration.VERTICAL)
        turisticPlacesList.addItemDecoration(mDividerItemDecoration)

        turisticPlacesList.adapter = TouristicPlaceAdapter(touristicPlacesList, this)

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

    fun addTouristicPlacesToList() {
        touristicPlacesList.add(TouristicPlace("Goya", "goya", "La descripción de Goya"))
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
        touristicPlacesList.add(TouristicPlace("3Catedral del salvador", "catedral_del_salvador","sioejfoise"))

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
