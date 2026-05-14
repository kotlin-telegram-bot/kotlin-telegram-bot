package com.github.kotlintelegrambot.entities.polls

import com.github.kotlintelegrambot.entities.files.Animation
import com.github.kotlintelegrambot.entities.files.LivePhoto
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.github.kotlintelegrambot.entities.files.Video
import com.google.gson.annotations.SerializedName

/**
 * Describes the media of a poll. (Bot API 10.0)
 *
 * See https://core.telegram.org/bots/api#pollmedia
 */
sealed class PollMedia {
    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /** Photo poll media. */
    data class Photo(
        @SerializedName("type") override val type: String = "photo",
        @SerializedName("photo") val photo: List<PhotoSize>,
    ) : PollMedia()

    /** Video poll media. */
    data class Video(
        @SerializedName("type") override val type: String = "video",
        @SerializedName("video") val video: com.github.kotlintelegrambot.entities.files.Video,
    ) : PollMedia()

    /** Live photo poll media. */
    data class LivePhoto(
        @SerializedName("type") override val type: String = "live_photo",
        @SerializedName("live_photo") val livePhoto: com.github.kotlintelegrambot.entities.files.LivePhoto,
    ) : PollMedia()

    /** Animation poll media. */
    data class Animation(
        @SerializedName("type") override val type: String = "animation",
        @SerializedName("animation") val animation: com.github.kotlintelegrambot.entities.files.Animation,
    ) : PollMedia()
}
