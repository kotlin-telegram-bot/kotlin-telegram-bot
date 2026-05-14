package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.entities.files.LivePhoto
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.google.gson.annotations.SerializedName

/**
 * Describes paid media. (Bot API 7.6)
 *
 * See https://core.telegram.org/bots/api#paidmedia
 */
sealed class PaidMedia {

    abstract val type: String

    /** The paid media isn't available before the payment. */
    data class Preview(
        @SerializedName("type") override val type: String = "preview",
        @SerializedName("width") val width: Int? = null,
        @SerializedName("height") val height: Int? = null,
        @SerializedName("duration") val duration: Int? = null,
    ) : PaidMedia()

    /** The paid media is a photo. */
    data class Photo(
        @SerializedName("type") override val type: String = "photo",
        @SerializedName("photo") val photo: List<PhotoSize>,
    ) : PaidMedia()

    /** The paid media is a video. */
    data class Video(
        @SerializedName("type") override val type: String = "video",
        @SerializedName("video") val video: com.github.kotlintelegrambot.entities.files.Video,
    ) : PaidMedia()

    /** The paid media is a live photo. (Bot API 10.0) */
    data class LivePhoto(
        @SerializedName("type") override val type: String = "live_photo",
        @SerializedName("live_photo") val livePhoto: LivePhoto,
    ) : PaidMedia()
}
