package com.example.wifind

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SignUpChoose : AppCompatActivity() {

    lateinit var sellerButton: Button
    lateinit var buyerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_choose)

        sellerButton = findViewById(R.id.sellerButton)
        buyerButton = findViewById(R.id.buyerButton)

        buyerButton.setOnClickListener {
            navigateToBuyerSignUp()
        }

    }

    fun navigateToBuyerSignUp() {
        startActivity(Intent(this, BuyerSignUpActivity::class.java))
    }
}