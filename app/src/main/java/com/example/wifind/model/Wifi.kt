package com.example.wifind.model

import com.example.wifind.Cryptography
import com.parse.ParseClassName
import com.parse.ParseGeoPoint
import com.parse.ParseObject
import com.parse.ParseUser
import com.parse.ktx.delegates.attribute
import com.parse.ktx.delegates.doubleAttribute
import com.parse.ktx.delegates.intAttribute
import com.parse.ktx.delegates.stringAttribute
import com.parse.ktx.getAs
import kotlin.reflect.KProperty

@ParseClassName("Wifi")
class Wifi : ParseObject() {
    var wifiName by stringAttribute()
    var price by doubleAttribute()
    var wifiPassword by encryptedStringAttribute()
    var wifiSpeed by intAttribute()
    var location by attribute<ParseGeoPoint>()
    var seller by attribute<ParseUser>()
}

class EncryptedStringParseDelegate(
    private val name: String?,
    private val filter: (String) -> String
) {

    operator fun getValue(parseObject: ParseObject, property: KProperty<*>): String {
        return Cryptography.decrypt(parseObject.getAs(name ?: property.name))
    }

    operator fun setValue(parseObject: ParseObject, property: KProperty<*>, value: String) {
        val encryptedValue = Cryptography.encrypt(filter.invoke(value))
        parseObject.put(name ?: property.name, encryptedValue)
    }
}

private inline fun encryptedStringAttribute(
    name: String? = null,
    noinline filter: (String) -> String = { it }
) = EncryptedStringParseDelegate(name, filter)
