package com.example.wifind.model

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser
import com.parse.ktx.delegates.attribute

@ParseClassName("Transaction")
class Transaction : ParseObject() {
    var user by attribute<ParseUser>()
    var wifi by attribute<Wifi>()
}