package com.example.wifind.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wifind.R
import com.example.wifind.RangeInputFilter
import com.example.wifind.WifiCardAdapter
import com.example.wifind.model.Wifi
import com.example.wifind.model.WifiCard
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.parse.ParseGeoPoint
import com.parse.ParseQuery
import com.parse.ParseUser


class MarketBoardActivity : AppCompatActivity() {

    private val TAG = "MYTAG"

    private lateinit var wifiRecyclerView: RecyclerView
    private lateinit var addWifiFab: FloatingActionButton
    private val wifiCards = mutableListOf<WifiCard>()
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_board)

        wifiRecyclerView = findViewById(R.id.recycler_view)
        addWifiFab = findViewById(R.id.fab)
        bottomNav = findViewById(R.id.bottom_nav)
        maybeShowAddWifiFab()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.marketplace -> true
                R.id.account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    true
                }

                else -> false
            }
        }

        addWifiFab.setOnClickListener {
            showAddWifiDialog()
        }

        wifiRecyclerView.adapter = WifiCardAdapter(wifiCards = wifiCards)
        wifiRecyclerView.layoutManager = LinearLayoutManager(this)

        refreshRecyclerView()
    }

    fun getLocationManager(): LocationManager {
        return getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun showAddWifiDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_wifi_add_edit, null)
        val latEditText = view.findViewById<EditText>(R.id.et_lat)
        val lonEditText = view.findViewById<EditText>(R.id.et_lon)
        latEditText.filters = arrayOf(RangeInputFilter(-90.0, 90.0))
        lonEditText.filters = arrayOf(RangeInputFilter(-180.0, 180.0))

        MaterialAlertDialogBuilder(this)
            .setView(view)
            .setTitle("Add Wifi")
            .setPositiveButton("Confirm") { _, _ ->
                val wifiName = view.findViewById<EditText>(R.id.et_name).text.toString()
                val wifiPassword = view.findViewById<EditText>(R.id.et_password).text.toString()
                val lat = latEditText.text.toString().toDouble()
                val lon = lonEditText.text.toString().toDouble()
                val price = view.findViewById<EditText>(R.id.et_price).text.toString().toDouble()
                val speed = view.findViewById<EditText>(R.id.et_speed).text.toString().toInt()

                Wifi().apply {
                    this.wifiName = wifiName
                    this.wifiPassword = wifiPassword
                    this.price = price
                    wifiSpeed = speed
                    location = ParseGeoPoint(lat, lon)
                    seller = ParseUser.getCurrentUser()
                }.save()
                refreshRecyclerView()
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    private fun maybeShowAddWifiFab() {
        val userType = ParseUser.getCurrentUser().getString("userType")
        if (userType == "Seller") {
            addWifiFab.isVisible = true
        }
    }

    private fun refreshRecyclerView() {
        Log.d(TAG, "MYTAG refreshRecyclerView()")
        val wifis = getAllWifis()
        getCurrentLocation(getLocationManager()) { location ->
            runOnUiThread {
                wifiCards.clear()
                wifiCards.addAll(
                    wifis
                        .map { it.toWifiCard(location) }
                        .sortedByDescending(WifiCard::distanceToWifi)
                )
                wifiRecyclerView.adapter?.notifyDataSetChanged()
                wifiRecyclerView.smoothScrollToPosition(0)
            }
        }
    }

    fun getAllWifis(): List<Wifi> {
        Log.d(TAG, "MYTAG getAllWifis()")
        return ParseQuery.getQuery<Wifi>("Wifi").find()
    }

    fun Wifi.toWifiCard(currentLocation: Location): WifiCard {
        val wifi = this
        val wifiLocation = Location("wifiLocation").apply {
            latitude = wifi.location.latitude
            longitude = wifi.location.longitude
        }
        return WifiCard(
            wifi = this,
            distanceToWifi = currentLocation.distanceTo(wifiLocation).toInt()
        )
    }

    @SuppressLint("MissingPermission") // already doing check
    private fun getCurrentLocation(locationManager: LocationManager, block: (Location) -> Unit) {
        Log.d(TAG, "MYTAG getCurrentLocation()")
        if (isLocationPermissionGranted()) {
            Log.d(TAG, "MYTAG getCurrentLocation() permission is granted")
            locationManager.getCurrentLocation(
                /* provider = */ LocationManager.NETWORK_PROVIDER,
                /* cancellationSignal = */ null,
                /* executor = */ { it.run() },
                /* consumer = */ { block(it) }
            )
        }
    }

    private fun isLocationPermissionGranted(): Boolean =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
}