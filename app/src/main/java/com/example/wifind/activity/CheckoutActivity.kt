package com.example.wifind.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wifind.model.StripeAccount
import com.example.wifind.model.Transaction
import com.example.wifind.model.Wifi
import com.parse.ParseCloud
import com.parse.ParseQuery
import com.parse.ParseUser
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject

class CheckoutActivity : AppCompatActivity() {
    lateinit var paymentSheet: PaymentSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val wifi = intent.getParcelableExtra<Wifi>("wifi")!!
        Log.d("MYTAG", "onCreate: wifiName: ${wifi.wifiName} seller: ${wifi.seller}")
        paymentSheet = PaymentSheet(this, callback = { onPaymentSheetResult(it, wifi) })
        val sellerAccountId = ParseQuery.getQuery(StripeAccount::class.java)
            .whereEqualTo("seller", wifi.seller)
            .first
            .accountId

        ParseCloud.callFunctionInBackground<String>(
            "paymentSheet",
            hashMapOf(
                "charge" to wifi.price,
                "accountId" to sellerAccountId
            )
        ) { jsonString, e ->
            Log.d("MYTAG", "paymentSheet(): response: $jsonString")
            val json = JSONObject(jsonString)
            val paymentIntentClientSecret = json.getString("paymentIntent")
            val publishableKey = json.getString("publishableKey")
            PaymentConfiguration.init(this, publishableKey)
            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret = paymentIntentClientSecret
            )
        }
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult, wifi: Wifi) {
        Log.d(
            "MYTAG",
            "onPaymentSheetResult(): paymentSheetResult=${paymentSheetResult::class.simpleName} : $paymentSheetResult"
        )

        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> setResult(RESULT_CANCELED)
            is PaymentSheetResult.Failed -> {
                Toast.makeText(
                    this,
                    "Error: ${paymentSheetResult.error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                setResult(RESULT_CANCELED)
            }

            is PaymentSheetResult.Completed -> {
                Transaction().apply {
                    buyer = ParseUser.getCurrentUser()
                    purchasedWifi = wifi
                }.save()
                val wifiPassword = ParseQuery.getQuery(Wifi::class.java)
                    .whereEqualTo("objectId", wifi.objectId)
                    .first
                    .wifiPassword
                setResult(RESULT_OK,
                    Intent()
                        .putExtra("wifiPassword", wifiPassword)
                        .putExtra("wifiId", wifi.objectId)
                )
            }
        }
        finish()

    }
}