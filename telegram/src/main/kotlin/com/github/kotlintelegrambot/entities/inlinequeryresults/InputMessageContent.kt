package com.github.kotlintelegrambot.entities.inlinequeryresults

import com.github.kotlintelegrambot.entities.ParseMode
import com.google.gson.annotations.SerializedName

sealed class InputMessageContent {
    data class Text(
        @SerializedName("message_text") val messageText: String,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("disable_web_page_preview") val disableWebPagePreview: Boolean? = null
    ) : InputMessageContent()

    data class Location(
        val latitude: Float,
        val longitude: Float,
        @SerializedName("live_period") val livePeriod: Int? = null,
        @SerializedName("proximity_alert_radius") val proximityAlertRadius: Int? = null
    ) : InputMessageContent()

    data class Venue(
        val latitude: Float,
        val longitude: Float,
        val title: String,
        val address: String,
        @SerializedName("foursquare_id") val foursquareId: String? = null,
        @SerializedName("foursquare_type") val foursquareType: String? = null,
        @SerializedName("google_place_id") val googlePlaceId: String? = null,
        @SerializedName("google_place_type") val googlePlaceType: String? = null
    ) : InputMessageContent()

    data class Contact(
        @SerializedName("phone_number") val phoneNumber: String,
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String? = null,
        val vcard: String? = null
    )
}
