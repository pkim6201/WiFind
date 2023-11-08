package com.example.wifind

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.wifind.WifiCardAdapter.TrailingButton.BUY
import com.example.wifind.WifiCardAdapter.TrailingButton.VIEW
import com.example.wifind.model.Transaction
import com.example.wifind.model.WifiCard
import com.parse.ParseQuery
import com.parse.ParseUser

class WifiCardAdapter(
    private val wifiCards: List<WifiCard>,
    private val onItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<WifiCardAdapter.ViewHolder>() {

    private var purchasedWifiIds: Set<String> = ParseQuery.getQuery(Transaction::class.java)
        .whereEqualTo("buyer", ParseUser.getCurrentUser())
        .find()
        .map { it.purchasedWifi.objectId }
        .toSet()

    interface OnItemClickListener {
        fun onEditClicked(wifiCard: WifiCard, position: Int)
        fun onDeleteClicked(wifiCard: WifiCard, position: Int)
        fun onBuyClicked(wifiCard: WifiCard)
        fun onViewClicked(wifiCard: WifiCard)
        fun onRateClicked(wifiCard: WifiCard)
    }

    enum class TrailingButton(val text: String) {
        VIEW("View"),
        BUY("Buy"),
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView = itemView.findViewById<TextView>(R.id.tvName)
        val distanceTextView = itemView.findViewById<TextView>(R.id.tvDistance)
        val speedTextView = itemView.findViewById<TextView>(R.id.tvSpeed)
        val priceTextView = itemView.findViewById<TextView>(R.id.tvPrice)
        val editButton = itemView.findViewById<ImageView>(R.id.edit_icon)
        val deleteButton = itemView.findViewById<ImageView>(R.id.delete_icon)
        val trailingButton = itemView.findViewById<Button>(R.id.bt_trailing)
        val rateButton = itemView.findViewById<Button>(R.id.bt_rate)
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
        val trailingButtonVisibility = if (didUserCreateWifiCard) View.GONE else View.VISIBLE
        val userPurchasedWifi = purchasedWifiIds.contains(wifiCard.wifi.objectId)
        val trailingButtonType = if (userPurchasedWifi) VIEW else BUY
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
            trailingButton.apply {
                visibility = trailingButtonVisibility
                text = trailingButtonType.text
                setOnClickListener {
                    when (trailingButtonType) {
                        VIEW -> onItemClickListener.onViewClicked(wifiCard)
                        BUY -> onItemClickListener.onBuyClicked(wifiCard)
                    }
                }
            }
            rateButton.apply {
                isVisible = userPurchasedWifi
                setOnClickListener { onItemClickListener.onRateClicked(wifiCard) }
            }
        }
    }

    override fun getItemCount(): Int = wifiCards.size
}