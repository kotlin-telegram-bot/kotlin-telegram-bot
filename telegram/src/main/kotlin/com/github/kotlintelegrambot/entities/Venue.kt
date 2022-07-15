package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class Venue(
    val location: Location,
    val title: String,
    val address: String,
    @Name("foursquare_id") val foursquareId: String? = null,
    @Name("foursquare_type") val foursquareType: String? = null,
    @Name("google_place_id") val googlePlaceId: String? = null,
    @Name("google_place_type") val googlePlaceType: String? = null
)
