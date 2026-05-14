package com.github.kotlintelegrambot.entities.payments

import com.google.gson.annotations.SerializedName

/**
 * Represents a paid live photo to be sent. (Bot API 10.0)
 *
 * See https://core.telegram.org/bots/api#inputpaidmedialivephoto
 */
data class InputPaidMediaLivePhoto(
    @SerializedName("type") val type: String = "live_photo",
    @SerializedName("video") val video: String,
    @SerializedName("photo") val photo: String,
)
