package com.example.wifind

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SignUpChoose : AppCompatActivity() {

    lateinit var sellerButton: Button
    lateinit var buyerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_choose)

        // TODO
        // 1. Initialize view references
        sellerButton = findViewById(R.id.sellerButton)
        buyerButton = findViewById(R.id.buyerButton)
        // 2. Setup click listeners on buttons
        sellerButton.setOnClickListener {
            navigateToSellerSignUp()
        }
        

        // 3. Handle click events on buttons
    }

    fun navigateToBuyerSignUp() {}
    fun navigateToSellerSignUp() {}
}