package com.example.wifind.model

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser
import com.parse.ktx.delegates.attribute
import com.parse.ktx.delegates.booleanAttribute

@ParseClassName("StripeAccount")
class StripeAccount : ParseObject() {
    var seller by attribute<ParseUser>()
    var isSetup by booleanAttribute()
}