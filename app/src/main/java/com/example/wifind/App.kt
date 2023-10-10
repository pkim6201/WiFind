package com.example.wifind

import android.app.Application
import com.example.wifind.model.Review
import com.example.wifind.model.Transaction
import com.example.wifind.model.Wifi
import com.parse.Parse
import com.parse.ParseObject

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        ParseObject.registerSubclass(Review::class.java)
        ParseObject.registerSubclass(Wifi::class.java)
        ParseObject.registerSubclass(Transaction::class.java)

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        )
    }
}