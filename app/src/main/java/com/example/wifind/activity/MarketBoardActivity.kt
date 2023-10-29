package com.example.wifind.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wifind.R
import com.example.wifind.WifiCardAdapter
import com.example.wifind.model.Wifi

class MarketBoardActivity : AppCompatActivity() {

    lateinit var wifiRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_board)

        wifiRecyclerView = findViewById(R.id.recycler_view)

        val wifis = listOf(
            Wifi().apply {
                wifiName = "name"
                price = 12.0
                wifiSpeed = 1
            }
        )
        wifiRecyclerView.adapter = WifiCardAdapter(wifis)
        wifiRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}