package com.example.wifind.model

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser
import com.parse.ktx.delegates.attribute

@ParseClassName("Transaction")
class Transaction : ParseObject() {
    var buyer by attribute<ParseUser>()
    var purchasedWifi by attribute<Wifi>()
}