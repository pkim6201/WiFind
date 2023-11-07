package com.example.wifind.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.browser.customtabs.CustomTabsIntent
import com.example.wifind.R
import com.parse.ParseCloud
import com.parse.ParseUser

class StripeSetupActivity : AppCompatActivity() {

    private lateinit var btOpenStripe: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_setup)

        btOpenStripe = findViewById(R.id.bt_open_stripe)

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
}