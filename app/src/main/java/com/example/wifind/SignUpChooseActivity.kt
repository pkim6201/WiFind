package com.example.wifind

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SignUpChooseActivity : AppCompatActivity() {

    private lateinit var sellerButton: Button
    private lateinit var buyerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_choose)

        sellerButton = findViewById(R.id.sellerButton)
        buyerButton = findViewById(R.id.buyerButton)

        buyerButton.setOnClickListener {
            navigateToBuyerSignUp()
        }

    }

    private fun navigateToBuyerSignUp() {
        startActivity(Intent(this, BuyerSignUpActivity::class.java))
    }
}