package com.android4dev.bottomsheet

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import android.widget.Toast
import com.android4dev.bottomsheet.models.TouristicPlace
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*


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
        setSupportActionBar(toolbar)

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
        // Loads animals into the ArrayList
        addTouristicPlacesToList()

        // Creates a vertical Layout Manager
        turisticPlacesList.layoutManager = LinearLayoutManager(this)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
        // turisticPlacesList.layoutManager = GridLayoutManager(this, 2)

        // Access the RecyclerView Adapter and load the data into it
        // turisticPlacesList.isNestedScrollingEnabled = false

        turisticPlacesList.adapter = TouristicPlaceAdapter(touristicPlacesList, this)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

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
        touristicPlacesList.add(TouristicPlace("Goya", "La descripción de Goya"))
        touristicPlacesList.add(TouristicPlace("La lonja", "La descripción de la lonjita.,.. vesas oiesjhofg sofihsoi haoiehoish faios hfis an ahsiou"))
        touristicPlacesList.add(TouristicPlace("El pilar", "no description el siguiente"))
        touristicPlacesList.add(TouristicPlace("La seo", ""))
        touristicPlacesList.add(TouristicPlace("Aljafería", "oisejhfgoiesahf hudas huiahas h kh sdfSAF OHDUIa 1235 32i5 hsdn"))
        touristicPlacesList.add(TouristicPlace("Catedral del salvador", "sioejfoise"))
        touristicPlacesList.add(TouristicPlace("2Goya", "La descripción de Goya"))
        touristicPlacesList.add(TouristicPlace("2La lonja", "La descripción de la lonjita.,.. vesas oiesjhofg sofihsoi haoiehoish faios hfis an ahsiou"))
        touristicPlacesList.add(TouristicPlace("2El pilar", "no description el siguiente"))
        touristicPlacesList.add(TouristicPlace("2La seo", ""))
        touristicPlacesList.add(TouristicPlace("2Aljafería", "oisejhfgoiesahf hudas huiahas h kh sdfSAF OHDUIa 1235 32i5 hsdn"))
        touristicPlacesList.add(TouristicPlace("2Catedral del salvador", "sioejfoise"))
        touristicPlacesList.add(TouristicPlace("3Goya", "La descripción de Goya"))
        touristicPlacesList.add(TouristicPlace("3La lonja", "La descripción de la lonjita.,.. vesas oiesjhofg sofihsoi haoiehoish faios hfis an ahsiou"))
        touristicPlacesList.add(TouristicPlace("3El pilar", "no description el siguiente"))
        touristicPlacesList.add(TouristicPlace("3La seo", ""))
        touristicPlacesList.add(TouristicPlace("3Aljafería", "oisejhfgoiesahf hudas huiahas h kh sdfSAF OHDUIa 1235 32i5 hsdn"))
        touristicPlacesList.add(TouristicPlace("3Catedral del salvador", "sioejfoise"))

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
