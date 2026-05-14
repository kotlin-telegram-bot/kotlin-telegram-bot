package com.github.kotlintelegrambot.entities.stories

import com.google.gson.annotations.SerializedName

/**
 * Describes the content of a story to post. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#inputstorycontent
 */
sealed class InputStoryContent {
    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /** Describes a photo to post as a story. */
    data class Photo(
        @SerializedName("type") override val type: String = "photo",
        /**
         * The photo to post as a story. The photo must be of the size 1080x1920 and must not exceed
         * 10 MB. The photo can't be reused and can only be uploaded as a new file, so you can pass
         * `attach://<file_attach_name>` if the photo was uploaded using multipart/form-data.
         */
        @SerializedName("photo") val photo: String,
    ) : InputStoryContent()

    /** Describes a video to post as a story. */
    data class Video(
        @SerializedName("type") override val type: String = "video",
        @SerializedName("video") val video: String,
        /** Precise duration of the video in seconds; 0-60. */
        @SerializedName("duration") val duration: Float? = null,
        /** Timestamp in seconds of the frame that will be used as the static cover for the story. Defaults to 0.0. */
        @SerializedName("cover_frame_timestamp") val coverFrameTimestamp: Float? = null,
        /** Pass `true` if the video has no sound. */
        @SerializedName("is_animation") val isAnimation: Boolean? = null,
    ) : InputStoryContent()
}
