package com.example.wifind

import java.util.Base64

object Cryptography {
    fun encrypt(text: String): String = Base64.getEncoder().encodeToString(text.toByteArray())

    fun decrypt(encryptedText: String?): String =
        encryptedText?.let { String(Base64.getDecoder().decode(it)) } ?: ""
}