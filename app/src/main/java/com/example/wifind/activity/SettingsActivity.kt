package com.example.wifind.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.wifind.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.ParseUser

class SettingsActivity : AppCompatActivity() {

    lateinit var btLogOut: Button
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btLogOut = findViewById(R.id.bt_logout)
        bottomNav = findViewById(R.id.bottom_nav)

        btLogOut.setOnClickListener {
            ParseUser.logOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.marketplace -> {
                    finish()
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.settings -> true

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.settings
    }

}