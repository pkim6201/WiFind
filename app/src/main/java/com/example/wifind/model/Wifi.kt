package com.example.wifind.model

import com.parse.ParseClassName
import com.parse.ParseGeoPoint
import com.parse.ParseObject
import com.parse.ParseUser
import com.parse.ktx.delegates.attribute
import com.parse.ktx.delegates.doubleAttribute
import com.parse.ktx.delegates.intAttribute
import com.parse.ktx.delegates.stringAttribute

@ParseClassName("Wifi")
class Wifi : ParseObject() {
    var wifiName by stringAttribute()
    var price by doubleAttribute()
    var wifiPassword by stringAttribute()
    var wifiSpeed by intAttribute()
    var location by attribute<ParseGeoPoint>()
    var seller by attribute<ParseUser>()
}