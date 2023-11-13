package com.example.wifind.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wifind.Marketboard
import com.example.wifind.R
import com.example.wifind.RangeInputFilter
import com.example.wifind.SortType
import com.example.wifind.WifiCardAdapter
import com.example.wifind.model.Review
import com.example.wifind.model.StripeAccount
import com.example.wifind.model.Wifi
import com.example.wifind.model.WifiCard
import com.example.wifind.model.userType
import com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.parse.ParseGeoPoint
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.Collections.addAll


class MarketBoardActivity : AppCompatActivity() {

    private val TAG = "MYTAG"

    private lateinit var wifiRecyclerView: RecyclerView
    private lateinit var addWifiFab: FloatingActionButton
    private lateinit var sortRadioGroup: RadioGroup
    private val marketboard = Marketboard()
    lateinit var bottomNav: BottomNavigationView
    private lateinit var wifiCardAdapter: WifiCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_board)

        maybeNavigateToStripeSignup()

        wifiRecyclerView = findViewById(R.id.recycler_view)
        addWifiFab = findViewById(R.id.fab)
        sortRadioGroup = findViewById(R.id.radio_group)
        bottomNav = findViewById(R.id.bottom_nav)
        maybeShowAddWifiFab()

        sortRadioGroup.setOnCheckedChangeListener { _, checkedId -> onSortOptionSelected(checkedId) }
        bottomNav.selectedItemId = R.id.marketplace
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.marketplace -> true
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }

        addWifiFab.setOnClickListener {
            showAddWifiDialog()
        }

        wifiCardAdapter = WifiCardAdapter(
            wifiCards = marketboard.wifiCards,
            object : WifiCardAdapter.OnItemClickListener {
                override fun onEditClicked(wifiCard: WifiCard, position: Int) {
                    onWifiCardEditClicked(wifiCard.wifi, position)
                }

                override fun onDeleteClicked(wifiCard: WifiCard, position: Int) {
                    onWifiCardDeleteClicked(wifiCard, position)
                }

                override fun onBuyClicked(wifiCard: WifiCard) {
                    val intent = Intent(this@MarketBoardActivity, CheckoutActivity::class.java)
                    intent.putExtra("wifi", wifiCard.wifi)
                    startActivityForResult(intent, CHECKOUT_ACTIVITY_REQUEST_CODE)
                }

                override fun onViewClicked(wifiCard: WifiCard) {
                    AlertDialog.Builder(this@MarketBoardActivity)
                        .setTitle("View Wifi Password")
                        .setMessage("Wifi Password: " + wifiCard.wifi.wifiPassword)
                        .setNegativeButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }

                override fun onRateClicked(wifiCard: WifiCard) {
                    val view = LayoutInflater.from(this@MarketBoardActivity)
                        .inflate(R.layout.dialog_add_rating, null)
                    val ratingBar = view.findViewById<RatingBar>(R.id.rating)
                    val etReview = view.findViewById<EditText>(R.id.et_review)

                    val dialog = MaterialAlertDialogBuilder(
                        this@MarketBoardActivity,
                        ThemeOverlay_Material3_MaterialAlertDialog_Centered
                    )
                        .setView(view)
                        .setTitle("Add Rating")
                        .setMessage("Rate the service by this Seller")
                        .setPositiveButton("Confirm") { _, _ ->
                            Review().apply {
                                userRating = ratingBar.rating.toInt()
                                userReview = etReview.text.toString()
                                seller = wifiCard.wifi.seller
                            }.save()
                        }
                        .setNegativeButton("Cancel") { _, _ -> }
                        .show()
                    dialog.findViewById<TextView>(android.R.id.message)?.gravity =
                        Gravity.CENTER_HORIZONTAL
                }
            }
        )
        wifiRecyclerView.adapter = wifiCardAdapter
        wifiRecyclerView.layoutManager = LinearLayoutManager(this)

        refreshRecyclerView()
    }

    private fun maybeNavigateToStripeSignup() {
        val user = ParseUser.getCurrentUser()
        if (user.userType != "Seller") return

        if (!didSellerSetupStripe(user)) {
            val intent = Intent(this, StripeSetupActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "onActivityResult: requestCode=$requestCode resultCode=$resultCode data=$data")
        if (requestCode != CHECKOUT_ACTIVITY_REQUEST_CODE || resultCode != Activity.RESULT_OK) return

        val wifiPassword = data?.getStringExtra("wifiPassword") ?: return
        val wifiId = data.getStringExtra("wifiId") ?: return
        wifiCardAdapter.addPurchasedWifi(wifiId)
        AlertDialog.Builder(this)
            .setTitle("Wifi Successfully Purchased")
            .setMessage("Wifi Password: $wifiPassword")
            .setNegativeButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun didSellerSetupStripe(user: ParseUser): Boolean {
        val stripeAccountForSeller = ParseQuery.getQuery<StripeAccount>("StripeAccount")
            .whereEqualTo("seller", user)
            .whereEqualTo("isSetup", true)
            .find()
        return !stripeAccountForSeller.isNullOrEmpty()
    }

    private fun onSortOptionSelected(checkedId: Int) {
        marketboard.sortWifisBy(
            if (checkedId == R.id.rb_distance)
                SortType.DISTANCE
            else
                SortType.PRICE
        )
        wifiRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.marketplace
    }

    fun onWifiCardEditClicked(wifi: Wifi, position: Int) {
        showAddEditWifiDialog(
            title = "Edit Wifi",
            wifi = wifi,
            onConfirmClicked = { name, password, price, speed, location, seller ->
                Wifi().apply {
                    objectId = wifi.objectId
                    wifiName = name
                    wifiPassword = password
                    this.price = price
                    wifiSpeed = speed
                    this.location = location
                    this.seller = seller
                }.save()
                refreshRecyclerView()
                wifiRecyclerView.adapter?.notifyItemChanged(position)
            }
        )
    }

    fun onWifiCardDeleteClicked(wifiCard: WifiCard, position: Int) {
        wifiCard.wifi.delete()
        marketboard.wifiCards.remove(wifiCard)
        wifiRecyclerView.adapter?.notifyItemRemoved(position)
    }

    fun getLocationManager(): LocationManager {
        return getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun showAddWifiDialog() {
        showAddEditWifiDialog(
            title = "Add Wifi",
            onConfirmClicked = { name, password, price, speed, location, seller ->
                Wifi().apply {
                    wifiName = name
                    wifiPassword = password
                    this.price = price
                    wifiSpeed = speed
                    this.location = location
                    this.seller = seller
                }.save()
                refreshRecyclerView()
            }
        )
    }

    private fun showAddEditWifiDialog(
        title: String,
        wifi: Wifi = Wifi(),
        onConfirmClicked: (name: String, password: String, price: Double, speed: Int, location: ParseGeoPoint, seller: ParseUser) -> Unit,
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
                        etName.text.toString(),
                        etPassword.text.toString(),
                        etPrice.text.toString().toDouble(),
                        etSpeed.text.toString().toInt(),
                        ParseGeoPoint(
                            etLat.text.toString().toDouble(),
                            etLon.text.toString().toDouble()
                        ),
                        ParseUser.getCurrentUser()
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Error: '${e.message}' please try again",
                        Toast.LENGTH_SHORT
                    ).show()
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
                marketboard.wifiCards.apply {
                    clear()
                    addAll(
                        wifis.map { it.toWifiCard(location) }
                    )
                }
                marketboard.sortWifisBy(SortType.DISTANCE)
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

    companion object {
        const val CHECKOUT_ACTIVITY_REQUEST_CODE = 42
    }
}