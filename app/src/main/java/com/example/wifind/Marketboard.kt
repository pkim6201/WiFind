package com.example.wifind

import com.example.wifind.model.WifiCard

enum class SortType {
    PRICE,
    DISTANCE
}

class Marketboard(
    val wifiCards: MutableList<WifiCard> = mutableListOf()
) {

    fun sortWifisBy(sortType: SortType) {
        when (sortType) {
            SortType.PRICE -> wifiCards.sortBy { it.wifi.price }
            SortType.DISTANCE -> wifiCards.sortBy { it.distanceToWifi }
        }
    }
}