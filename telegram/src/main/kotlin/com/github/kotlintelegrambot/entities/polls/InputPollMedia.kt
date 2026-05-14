package com.github.kotlintelegrambot.entities.polls

import com.google.gson.annotations.SerializedName

/**
 * Describes the input media of a poll. (Bot API 10.0)
 *
 * See https://core.telegram.org/bots/api#inputpollmedia
 */
sealed class InputPollMedia {

    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /** File identifier, HTTP URL or attach://<file_attach_name> reference. */
    abstract val media: String

    /** Photo input poll media. */
    data class Photo(
        @SerializedName("type") override val type: String = "photo",
        @SerializedName("media") override val media: String,
    ) : InputPollMedia()

    /** Video input poll media. */
    data class Video(
        @SerializedName("type") override val type: String = "video",
        @SerializedName("media") override val media: String,
    ) : InputPollMedia()

    /** Live photo input poll media. */
    data class LivePhoto(
        @SerializedName("type") override val type: String = "live_photo",
        @SerializedName("media") override val media: String,
    ) : InputPollMedia()

    /** Animation input poll media. */
    data class Animation(
        @SerializedName("type") override val type: String = "animation",
        @SerializedName("media") override val media: String,
    ) : InputPollMedia()
}
