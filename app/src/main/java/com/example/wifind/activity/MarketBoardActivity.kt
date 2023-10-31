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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
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

        bottomNav.selectedItemId = R.id.marketplace
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.marketplace -> true
                R.id.account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }

        addWifiFab.setOnClickListener {
            showAddWifiDialog()
        }

        wifiRecyclerView.adapter = WifiCardAdapter(
            wifiCards = wifiCards,
            object : WifiCardAdapter.OnItemClickListener {
                override fun onEditClicked(wifiCard: WifiCard, position: Int) {
                    onWifiCardEditClicked(wifiCard.wifi, position)
                }

                override fun onDeleteClicked(wifiCard: WifiCard, position: Int) {
                    onWifiCardDeleteClicked(wifiCard, position)
                }

            }
        )
        wifiRecyclerView.layoutManager = LinearLayoutManager(this)

        refreshRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.marketplace
    }

    fun onWifiCardEditClicked(wifi: Wifi, position: Int) {
        showAddEditWifiDialog(
            title = "Edit Wifi",
            wifi = wifi,
            onConfirmClicked = { wifi ->
                wifi.save()
                refreshRecyclerView()
                wifiRecyclerView.adapter?.notifyItemChanged(position)
            }
        )
    }

    fun onWifiCardDeleteClicked(wifiCard: WifiCard, position: Int) {
        wifiCard.wifi.delete()
        wifiCards.remove(wifiCard)
        wifiRecyclerView.adapter?.notifyItemRemoved(position)
    }

    fun getLocationManager(): LocationManager {
        return getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun showAddWifiDialog() {
        showAddEditWifiDialog(
            title = "Add Wifi",
            onConfirmClicked = { wifi ->
                wifi.save()
                refreshRecyclerView()
            }
        )
    }

    private fun showAddEditWifiDialog(
        title: String,
        wifi: Wifi = Wifi(),
        onConfirmClicked: (wifi: Wifi) -> Unit,
    ) {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_wifi_add_edit, null)
        val etLat = view.findViewById<EditText>(R.id.et_lat)
        val etLon = view.findViewById<EditText>(R.id.et_lon)
        val etName = view.findViewById<EditText>(R.id.et_name)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val etPrice = view.findViewById<EditText>(R.id.et_price)
        val etSpeed = view.findViewById<EditText>(R.id.et_speed)

        etLat.filters = arrayOf(RangeInputFilter(-90.0, 90.0))
        etLon.filters = arrayOf(RangeInputFilter(-180.0, 180.0))
        etLat.setText(wifi.location?.latitude.toString() ?: "")
        etLon.setText(wifi.location?.longitude.toString() ?: "")
        etName.setText(wifi.wifiName)
        etPassword.setText(wifi.wifiPassword)
        etPrice.setText(if (wifi.price == 0.0) "" else wifi.price.toString())
        etSpeed.setText(if (wifi.wifiSpeed == 0) "" else wifi.wifiSpeed.toString())

        MaterialAlertDialogBuilder(this)
            .setView(view)
            .setTitle(title)
            .setPositiveButton("Confirm") { _, _ ->
                try {
                    onConfirmClicked(
                        wifi.apply {
                            this.wifiName = etName.text.toString()
                            this.wifiPassword = etPassword.text.toString()
                            this.price = etPrice.text.toString().toDouble()
                            wifiSpeed = etSpeed.text.toString().toInt()
                            location = ParseGeoPoint(
                                etLat.text.toString().toDouble(),
                                etLon.text.toString().toDouble()
                            )
                            seller = ParseUser.getCurrentUser()
                        }
                    )
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: '${e.message}' please try again", Toast.LENGTH_SHORT).show()
                }
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