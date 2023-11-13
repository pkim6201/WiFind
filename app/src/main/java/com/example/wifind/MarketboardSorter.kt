package com.example.wifind

import com.example.wifind.model.WifiCard

enum class SortType {
    PRICE,
    DISTANCE
}

object MarketboardSorter {
    fun MutableList<WifiCard>.sortWifisBy(sortType: SortType) {
        when (sortType) {
            SortType.PRICE -> this.sortBy { it.wifi.price }
            SortType.DISTANCE -> this.sortBy { it.distanceToWifi }
        }
    }
}