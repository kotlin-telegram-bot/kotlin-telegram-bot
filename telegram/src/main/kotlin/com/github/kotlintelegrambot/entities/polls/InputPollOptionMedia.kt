package com.github.kotlintelegrambot.entities.polls

import com.google.gson.annotations.SerializedName

/**
 * Describes the input media of a poll option. (Bot API 10.0)
 *
 * See https://core.telegram.org/bots/api#inputpolloptionmedia
 */
sealed class InputPollOptionMedia {

    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /** File identifier, HTTP URL or attach://<file_attach_name> reference. */
    abstract val media: String

    /** Photo input poll option media. */
    data class Photo(
        @SerializedName("type") override val type: String = "photo",
        @SerializedName("media") override val media: String,
    ) : InputPollOptionMedia()

    /** Video input poll option media. */
    data class Video(
        @SerializedName("type") override val type: String = "video",
        @SerializedName("media") override val media: String,
    ) : InputPollOptionMedia()

    /** Live photo input poll option media. */
    data class LivePhoto(
        @SerializedName("type") override val type: String = "live_photo",
        @SerializedName("media") override val media: String,
    ) : InputPollOptionMedia()

    /** Animation input poll option media. */
    data class Animation(
        @SerializedName("type") override val type: String = "animation",
        @SerializedName("media") override val media: String,
    ) : InputPollOptionMedia()
}
