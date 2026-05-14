package com.github.kotlintelegrambot.entities.inputmedia

import com.google.gson.annotations.SerializedName

/**
 * Represents a location to be sent. (Bot API 10.0)
 *
 * Standalone definition: not yet wired into the existing [InputMedia] sealed type. See follow-up
 * notes in the PR description.
 *
 * See https://core.telegram.org/bots/api#inputmedialocation
 */
data class InputMediaLocation(
    @SerializedName("type") val type: String = "location",
    @SerializedName("latitude") val latitude: Float,
    @SerializedName("longitude") val longitude: Float,
    @SerializedName("horizontal_accuracy") val horizontalAccuracy: Float? = null,
    @SerializedName("live_period") val livePeriod: Int? = null,
    @SerializedName("heading") val heading: Int? = null,
    @SerializedName("proximity_alert_radius") val proximityAlertRadius: Int? = null,
)
