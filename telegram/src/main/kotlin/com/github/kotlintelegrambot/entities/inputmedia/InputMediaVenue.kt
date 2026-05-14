package com.github.kotlintelegrambot.entities.inputmedia

import com.google.gson.annotations.SerializedName

/**
 * Represents a venue to be sent. (Bot API 10.0)
 *
 * Standalone definition: not yet wired into the existing [InputMedia] sealed type. See follow-up
 * notes in the PR description.
 *
 * See https://core.telegram.org/bots/api#inputmediavenue
 */
data class InputMediaVenue(
    @SerializedName("type") val type: String = "venue",
    @SerializedName("latitude") val latitude: Float,
    @SerializedName("longitude") val longitude: Float,
    @SerializedName("title") val title: String,
    @SerializedName("address") val address: String,
    @SerializedName("foursquare_id") val foursquareId: String? = null,
    @SerializedName("foursquare_type") val foursquareType: String? = null,
    @SerializedName("google_place_id") val googlePlaceId: String? = null,
    @SerializedName("google_place_type") val googlePlaceType: String? = null,
)
