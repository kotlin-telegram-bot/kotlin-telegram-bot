package com.github.kotlintelegrambot.entities.payments

import com.google.gson.annotations.SerializedName

/**
 * Describes the paid media to be sent. Currently it can be one of [Photo] or [Video]. (Bot API 7.6)
 *
 * The `media` field expects either a file_id of a previously uploaded file, an HTTP URL for
 * Telegram to fetch from the Internet, or `attach://<file_attach_name>` to upload a new file.
 *
 * See https://core.telegram.org/bots/api#inputpaidmedia
 */
sealed class InputPaidMedia {

    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /** A photo to be sent as paid media. */
    data class Photo(
        @SerializedName("type") override val type: String = "photo",
        @SerializedName("media") val media: String,
    ) : InputPaidMedia()

    /** A video to be sent as paid media. */
    data class Video(
        @SerializedName("type") override val type: String = "video",
        @SerializedName("media") val media: String,
        @SerializedName("thumbnail") val thumbnail: String? = null,
        @SerializedName("cover") val cover: String? = null,
        @SerializedName("start_timestamp") val startTimestamp: Int? = null,
        @SerializedName("width") val width: Int? = null,
        @SerializedName("height") val height: Int? = null,
        @SerializedName("duration") val duration: Int? = null,
        @SerializedName("supports_streaming") val supportsStreaming: Boolean? = null,
    ) : InputPaidMedia()
}
