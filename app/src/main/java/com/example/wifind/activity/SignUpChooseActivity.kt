package com.example.wifind.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.wifind.R

class SignUpChooseActivity : AppCompatActivity() {

    private lateinit var sellerButton: Button
    private lateinit var buyerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_choose)

        sellerButton = findViewById(R.id.sellerButton)
        buyerButton = findViewById(R.id.buyerButton)

        buyerButton.setOnClickListener {
            navigateToActivity(BuyerSignUpActivity::class.java)
        }
        sellerButton.setOnClickListener {
            navigateToActivity(SellerSignUpActivity::class.java)
        }

    }

    private fun navigateToActivity(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }
}
