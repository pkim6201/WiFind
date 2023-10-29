package com.example.wifind

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wifind.model.Wifi

class WifiCardAdapter(
    private val wifis: List<Wifi>
) : RecyclerView.Adapter<WifiCardAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView = itemView.findViewById<TextView>(R.id.tvName)
        val distanceTextView = itemView.findViewById<TextView>(R.id.tvDistance)
        val speedTextView = itemView.findViewById<TextView>(R.id.tvSpeed)
        val priceTextView = itemView.findViewById<TextView>(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiCardAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val wifiView = inflater.inflate(R.layout.layout_wifi_item, parent, false)
        return ViewHolder(wifiView)
    }

    override fun onBindViewHolder(viewHolder: WifiCardAdapter.ViewHolder, position: Int) {
        val wifi = wifis[position]
        viewHolder.apply {
            nameTextView.text = wifi.wifiName
            distanceTextView.text = "" // TODO
            speedTextView.text = "Speed: " + wifi.wifiSpeed.toString()
            priceTextView.text = "Price: $" + wifi.price.toString()
        }
    }

    override fun getItemCount(): Int = wifis.size
}