package com.example.wifind.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wifind.R
import com.example.wifind.model.StripeAccount
import com.parse.ParseQuery
import com.parse.ParseUser

class StripeReturnActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_return)

        updateStripeAccount()
        navigateToMarketboard()
    }

    private fun navigateToMarketboard() {
        val intent = Intent(this, MarketBoardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun updateStripeAccount() {
        ParseQuery.getQuery(StripeAccount::class.java)
            .whereEqualTo("seller", ParseUser.getCurrentUser())
            .first
            .apply { isSetup = true }
            .save()
    }
}