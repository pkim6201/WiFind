package com.example.wifind.model

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser
import com.parse.ktx.delegates.attribute
import com.parse.ktx.delegates.intAttribute
import com.parse.ktx.delegates.stringAttribute

@ParseClassName("Review")
class Review : ParseObject() {
    var userRating by intAttribute()
    var userReview by stringAttribute()
    var wifi by attribute<Wifi>()
    var user by attribute<ParseUser>()
}