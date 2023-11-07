package com.example.wifind.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.browser.customtabs.CustomTabsIntent
import com.example.wifind.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.ParseCloud
import com.parse.ParseUser

class StripeSetupActivity : AppCompatActivity() {

    private lateinit var btOpenStripe: Button
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_setup)

        btOpenStripe = findViewById(R.id.bt_open_stripe)
        bottomNav = findViewById(R.id.bottom_nav)

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

        btOpenStripe.setOnClickListener {
            val sellerId = ParseUser.getCurrentUser().fetchIfNeeded().objectId
            println("MYTAG sellerId: $sellerId")
            ParseCloud.callFunctionInBackground<String>(
                "createStripeAccount",
                hashMapOf("sellerId" to sellerId)
            ) { url, e ->
                CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.marketplace
    }
}