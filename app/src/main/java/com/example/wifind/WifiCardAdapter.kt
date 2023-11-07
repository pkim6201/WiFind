package com.example.wifind

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wifind.model.WifiCard
import com.parse.ParseUser

class WifiCardAdapter(
    private val wifiCards: List<WifiCard>,
    private val onItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<WifiCardAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onEditClicked(wifiCard: WifiCard, position: Int)
        fun onDeleteClicked(wifiCard: WifiCard, position: Int)
        fun onBuyClicked(wifiCard: WifiCard)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView = itemView.findViewById<TextView>(R.id.tvName)
        val distanceTextView = itemView.findViewById<TextView>(R.id.tvDistance)
        val speedTextView = itemView.findViewById<TextView>(R.id.tvSpeed)
        val priceTextView = itemView.findViewById<TextView>(R.id.tvPrice)
        val editButton = itemView.findViewById<ImageView>(R.id.edit_icon)
        val deleteButton = itemView.findViewById<ImageView>(R.id.delete_icon)
        val buyButton = itemView.findViewById<Button>(R.id.bt_buy)
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
        val didUserCreateWifiCard = wifiCard.wifi.seller.hasSameId(ParseUser.getCurrentUser())
        val iconVisibility = if (didUserCreateWifiCard) View.VISIBLE else View.GONE
        val buyButtonVisibility = if (didUserCreateWifiCard) View.GONE else View.VISIBLE
        viewHolder.apply {
            nameTextView.text = wifiCard.wifi.wifiName
            distanceTextView.text = "Distance: " + wifiCard.distanceToWifi.toString() + " meters"
            speedTextView.text = "Wifi Speed: " + wifiCard.wifi.wifiSpeed.toString()
            priceTextView.text = "Price: $" + wifiCard.wifi.price.toString()
            editButton.apply {
                visibility = iconVisibility
                setOnClickListener { onItemClickListener.onEditClicked(wifiCard, position) }
            }
            deleteButton.apply {
                visibility = iconVisibility
                setOnClickListener { onItemClickListener.onDeleteClicked(wifiCard, position) }
            }
            buyButton.apply {
                visibility = buyButtonVisibility
                setOnClickListener { onItemClickListener.onBuyClicked(wifiCard) }
            }
        }
    }

    override fun getItemCount(): Int = wifiCards.size
}