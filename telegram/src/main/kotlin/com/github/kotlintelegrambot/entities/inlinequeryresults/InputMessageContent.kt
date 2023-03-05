package com.github.kotlintelegrambot.entities.inlinequeryresults

import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.payments.LabeledPrice
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
    ) : InputMessageContent()

    data class Invoice(
        val title: String,
        val description: String,
        val payload: String,
        @SerializedName("provider_token") val providerToken: String,
        val currency: String,
        val prices: List<LabeledPrice>,
        @SerializedName("max_tip_amount") val maxTipAmount: Int? = null,
        @SerializedName("suggested_tip_amounts") val suggestedTipAmounts: List<Int>? = null,
        @SerializedName("provider_data") val providerData: String? = null,
        @SerializedName("photo_url") val photoUrl: String? = null,
        @SerializedName("photo_size") val photoSize: Int? = null,
        @SerializedName("photo_width") val photoWidth: Int? = null,
        @SerializedName("photo_height") val photoHeight: Int? = null,
        @SerializedName("need_name") val needName: Boolean? = null,
        @SerializedName("need_phone_number") val needPhoneNumber: Boolean? = null,
        @SerializedName("need_email") val needEmail: Boolean? = null,
        @SerializedName("need_shipping_address") val needShippingAddress: Boolean? = null,
        @SerializedName("send_phone_number_to_provider") val sendPhoneNumberToProvider: Boolean? = null,
        @SerializedName("send_email_to_provider") val sendEmailToProvider: Boolean? = null,
        @SerializedName("is_flexible") val isFlexible: Boolean? = null
    ) : InputMessageContent()
}
