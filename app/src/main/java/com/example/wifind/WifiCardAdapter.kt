package com.example.wifind

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wifind.model.WifiCard
import com.parse.ParseUser

class WifiCardAdapter(
    private val wifiCards: List<WifiCard>
) : RecyclerView.Adapter<WifiCardAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView = itemView.findViewById<TextView>(R.id.tvName)
        val distanceTextView = itemView.findViewById<TextView>(R.id.tvDistance)
        val speedTextView = itemView.findViewById<TextView>(R.id.tvSpeed)
        val priceTextView = itemView.findViewById<TextView>(R.id.tvPrice)
        val editButton = itemView.findViewById<ImageView>(R.id.edit_icon)
        val deleteButton = itemView.findViewById<ImageView>(R.id.delete_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiCardAdapter.ViewHolder {
        Log.d("MYTAG", "onCreateViewHolder() $wifiCards")
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val wifiView = inflater.inflate(R.layout.layout_wifi_item, parent, false)
        return ViewHolder(wifiView)
    }

    override fun onBindViewHolder(viewHolder: WifiCardAdapter.ViewHolder, position: Int) {
        val wifiCard = wifiCards[position]
        val iconVisibility = if (wifiCard.wifi.seller.hasSameId(ParseUser.getCurrentUser())) View.VISIBLE else View.GONE
        viewHolder.apply {
            nameTextView.text = wifiCard.wifi.wifiName
            distanceTextView.text = "Distance: " + wifiCard.distanceToWifi.toString() + " meters"
            speedTextView.text = "Wifi Speed: " + wifiCard.wifi.wifiSpeed.toString()
            priceTextView.text = "Price: $" + wifiCard.wifi.price.toString()
            editButton.visibility = iconVisibility
            deleteButton.visibility = iconVisibility
        }
    }

    override fun getItemCount(): Int = wifiCards.size
}