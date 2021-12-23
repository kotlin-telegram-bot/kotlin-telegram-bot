package com.github.kotlintelegrambot.entities.inlinequeryresults

import com.github.kotlintelegrambot.entities.ParseMode
import com.google.gson.annotations.SerializedName

public sealed class InputMessageContent {
    public data class Text(
        @SerializedName("message_text") val messageText: String,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("disable_web_page_preview") val disableWebPagePreview: Boolean? = null
    ) : InputMessageContent()

    public data class Location(
        val latitude: Float,
        val longitude: Float,
        @SerializedName("live_period") val livePeriod: Int? = null
    ) : InputMessageContent()

    public data class Venue(
        val latitude: Float,
        val longitude: Float,
        val title: String,
        val address: String,
        @SerializedName("foursquare_id") val foursquareId: String? = null,
        @SerializedName("foursquare_type") val foursquareType: String? = null
    ) : InputMessageContent()

    public data class Contact(
        @SerializedName("phone_number") val phoneNumber: String,
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String? = null,
        val vcard: String? = null
    )
}
