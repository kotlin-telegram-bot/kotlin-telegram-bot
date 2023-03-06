package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class Location(
    val longitude: Float,
    val latitude: Float,
    @Name("live_period") val livePeriod: Int? = null,
    @Name("proximity_alert_radius") val proximityAlertRadius: Int? = null
)
