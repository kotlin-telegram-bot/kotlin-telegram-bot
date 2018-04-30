package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class KeyboardButton(
    val text: String,
    @Name("request_contact") val requestContact: Boolean = false,
    @Name("request_location") val requestLocation: Boolean = false
)