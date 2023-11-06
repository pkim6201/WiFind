package com.example.wifind

import android.app.Application
import com.example.wifind.model.Review
import com.example.wifind.model.StripeAccount
import com.example.wifind.model.Transaction
import com.example.wifind.model.Wifi
import com.parse.Parse
import com.parse.ParseObject
import com.stripe.android.PaymentConfiguration

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        arrayOf(Review::class, Wifi::class, Transaction::class, StripeAccount::class).forEach {
            ParseObject.registerSubclass(it.java)
        }

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        )
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51GrSuPCwLbRg8FLANkdO9QhAoYhvds16JDdIc6decB2PdOKXsNnGAgYKSCjw3n9AfIh3Kn2sNqI6ExMb6jLUl6wr00BfuIrQ6H"
        )
    }
}