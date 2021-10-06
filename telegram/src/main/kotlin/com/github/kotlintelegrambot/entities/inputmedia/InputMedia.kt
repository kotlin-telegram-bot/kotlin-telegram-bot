package com.github.kotlintelegrambot.entities.inputmedia

import com.github.kotlintelegrambot.entities.TelegramFile
import com.google.gson.annotations.SerializedName

/**
 * Represents the content of a media message to be sent.
 * https://core.telegram.org/bots/api#inputmedia
 */
sealed interface InputMedia {
    val type: String
    val media: TelegramFile
    val caption: String?
    val parseMode: String?
}

/**
 * Interface to mark all the media types that can be sent within a group of media for
 * operations like `sendMediaGroup`.
 */
sealed interface GroupableMedia : InputMedia

class MediaGroup private constructor(val medias: Array<out GroupableMedia>) {
    init {
        require(medias.size in 2..10) { "media groups must include 2-10 items" }
    }

    companion object {
        fun from(vararg media: GroupableMedia): MediaGroup = MediaGroup(generateUniqueNamesForNamelessByteArrays(media))

        private fun generateUniqueNamesForNamelessByteArrays(media: Array<out GroupableMedia>): Array<GroupableMedia> {
            val mediaNames = media
                .map { it.media }
                .mapNotNull {
                    when (it) {
                        is TelegramFile.ByFile -> it.file.name
                        is TelegramFile.ByByteArray -> it.filename
                        else -> null
                    }
                }

            var counter = 0
            return media.map {
                val m = it.media
                if (m is TelegramFile.ByByteArray && m.filename == null) {
                    var name = "noname${counter++}"
                    while (name in mediaNames) { //in case if there already was a file with explicit name "nonameX"
                        name = "noname${counter++}"
                    }
                    when (it) {
                        is InputMediaAudio -> it.copy(media = m.copy(filename = name))
                        is InputMediaDocument -> it.copy(media = m.copy(filename = name))
                        is InputMediaPhoto -> it.copy(media = m.copy(filename = name))
                        is InputMediaVideo -> it.copy(media = m.copy(filename = name))
                    }
                } else it
            }.toTypedArray()
        }
    }
}

/**
 * Represents a photo to be sent. Can be sent as part of a group of media.
 * https://core.telegram.org/bots/api#inputmediaphoto
 */
data class InputMediaPhoto(
    @SerializedName(InputMediaFields.MEDIA) override val media: TelegramFile,
    @SerializedName(InputMediaFields.CAPTION) override val caption: String? = null,
    @SerializedName(InputMediaFields.PARSE_MODE) override val parseMode: String? = null,
) : GroupableMedia {
    @SerializedName(InputMediaFields.TYPE)
    override val type: String = InputMediaTypes.PHOTO
}

/**
 * Represents a video to be sent. Can be sent as part of a group of media.
 * https://core.telegram.org/bots/api#inputmediavideo
 */
data class InputMediaVideo(
    @SerializedName(InputMediaFields.MEDIA) override val media: TelegramFile,
    @SerializedName(InputMediaFields.CAPTION) override val caption: String? = null,
    @SerializedName(InputMediaFields.PARSE_MODE) override val parseMode: String? = null,
    @SerializedName(InputMediaFields.THUMB) val thumb: TelegramFile.ByFile? = null,
    @SerializedName(InputMediaFields.WIDTH) val width: Int? = null,
    @SerializedName(InputMediaFields.HEIGHT) val height: Int? = null,
    @SerializedName(InputMediaFields.DURATION) val duration: Int? = null,
    @SerializedName(InputMediaFields.SUPPORTS_STREAMING) val supportsStreaming: Boolean? = null,
) : GroupableMedia {
    @SerializedName(InputMediaFields.TYPE)
    override val type: String = InputMediaTypes.VIDEO
}

/**
 * Represents an animation file (GIF or H.264/MPEG-4 AVC video without sound) to be sent.
 * https://core.telegram.org/bots/api#inputmediaanimation
 */
data class InputMediaAnimation(
    @SerializedName(InputMediaFields.MEDIA) override val media: TelegramFile,
    @SerializedName(InputMediaFields.CAPTION) override val caption: String? = null,
    @SerializedName(InputMediaFields.PARSE_MODE) override val parseMode: String? = null,
    @SerializedName(InputMediaFields.THUMB) val thumb: TelegramFile.ByFile? = null,
    @SerializedName(InputMediaFields.WIDTH) val width: Int? = null,
    @SerializedName(InputMediaFields.HEIGHT) val height: Int? = null,
    @SerializedName(InputMediaFields.DURATION) val duration: Int? = null,
) : InputMedia {
    @SerializedName(InputMediaFields.TYPE)
    override val type: String = InputMediaTypes.ANIMATION
}

/**
 * Represents an audio file to be treated as music to be sent.
 * https://core.telegram.org/bots/api#inputmediaaudio
 */
data class InputMediaAudio(
    @SerializedName(InputMediaFields.MEDIA) override val media: TelegramFile,
    @SerializedName(InputMediaFields.CAPTION) override val caption: String? = null,
    @SerializedName(InputMediaFields.PARSE_MODE) override val parseMode: String? = null,
    @SerializedName(InputMediaFields.THUMB) val thumb: TelegramFile.ByFile? = null,
    @SerializedName(InputMediaFields.DURATION) val duration: Int? = null,
    @SerializedName(InputMediaFields.PERFORMER) val performer: String? = null,
    @SerializedName(InputMediaFields.TITLE) val title: String? = null,
) : GroupableMedia {
    @SerializedName(InputMediaFields.TYPE)
    override val type: String = InputMediaTypes.AUDIO
}

/**
 * Represents a general file to be sent.
 * https://core.telegram.org/bots/api#inputmediadocument
 */
data class InputMediaDocument(
    @SerializedName(InputMediaFields.MEDIA) override val media: TelegramFile,
    @SerializedName(InputMediaFields.CAPTION) override val caption: String? = null,
    @SerializedName(InputMediaFields.PARSE_MODE) override val parseMode: String? = null,
    @SerializedName(InputMediaFields.THUMB) val thumb: TelegramFile.ByFile? = null,
    @SerializedName(InputMediaFields.DISABLE_CONTENT_TYPE_DETECTION) val disableContentTypeDetection: Boolean? = null,
) : GroupableMedia {
    @SerializedName(InputMediaFields.TYPE)
    override val type: String = InputMediaTypes.DOCUMENT
}
