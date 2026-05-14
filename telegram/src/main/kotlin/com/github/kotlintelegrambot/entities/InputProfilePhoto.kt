package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Describes a profile photo to set. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#inputprofilephoto
 */
sealed class InputProfilePhoto {
    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /** A static profile photo in the .JPG format. */
    data class Static(
        @SerializedName("type") override val type: String = "static",
        /**
         * The static profile photo. Profile photos can't be reused and can only be uploaded as a new
         * file, so you can pass `attach://<file_attach_name>` if the photo was uploaded using
         * multipart/form-data under `<file_attach_name>`.
         */
        @SerializedName("photo") val photo: String,
    ) : InputProfilePhoto()

    /** An animated profile photo in the MPEG4 format. */
    data class Animated(
        @SerializedName("type") override val type: String = "animated",
        @SerializedName("animation") val animation: String,
        /** Timestamp in seconds of the frame that will be used as the static profile photo. Defaults to 0.0. */
        @SerializedName("main_frame_timestamp") val mainFrameTimestamp: Float? = null,
    ) : InputProfilePhoto()
}
