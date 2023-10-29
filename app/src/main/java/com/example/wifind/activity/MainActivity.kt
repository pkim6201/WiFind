package com.example.wifind.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.wifind.R
import com.google.android.material.textfield.TextInputEditText
import com.parse.ParseException
import com.parse.ParseUser

class MainActivity : AppCompatActivity() {

    private var username: TextInputEditText? = null
    private var password: TextInputEditText? = null
    private var login: Button? = null
    private var navigatesignup: Button? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        progressDialog = ProgressDialog(this@MainActivity)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        navigatesignup = findViewById(R.id.navigatesignup)

        maybeRequestLocationPermission()

        login?.setOnClickListener {
            login(
                username?.text.toString(),
                password?.text.toString()
            )
        }

        navigatesignup?.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    SignUpActivity::class.java
                )
            )
        }
    }

    fun login(username: String, password: String) {
        progressDialog?.show()
        ParseUser.logInInBackground(
            username,
            password
        ) { parseUser: ParseUser?, parseException: ParseException? ->
            progressDialog?.dismiss()
            if (parseUser != null) {
                val intent = Intent(this, MarketBoardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                ParseUser.logOut()
                if (parseException != null) {
                    Toast.makeText(this, parseException.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun maybeRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 89
            )
        }
    }
}