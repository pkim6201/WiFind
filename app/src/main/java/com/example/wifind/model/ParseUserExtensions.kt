package com.example.wifind.model

import com.parse.ParseUser
import com.parse.ktx.delegates.stringAttribute

var ParseUser.userType by stringAttribute()