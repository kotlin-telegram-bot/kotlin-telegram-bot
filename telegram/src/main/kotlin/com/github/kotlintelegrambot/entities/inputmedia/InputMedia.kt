package com.github.kotlintelegrambot.entities.inputmedia

import com.github.kotlintelegrambot.entities.TelegramFile
import com.google.gson.annotations.SerializedName

/**
 * Represents the content of a media message to be sent.
 * https://core.telegram.org/bots/api#inputmedia
 */
public sealed class InputMedia {
    public abstract val type: String
    public abstract val media: TelegramFile
    public abstract val caption: String?
    public abstract val parseMode: String?
}

/**
 * Interface to mark all the media types that can be sent within a group of media for
 * operations like `sendMediaGroup`.
 */
public interface GroupableMedia

public class MediaGroup private constructor(public val medias: Array<out GroupableMedia>) {
    init {
        if (!(2..10).contains(medias.size)) {
            throw IllegalArgumentException("media groups must include 2-10 items")
        }
    }

    public companion object {
        public fun from(vararg media: GroupableMedia): MediaGroup = MediaGroup(media)
    }
}

/**
 * Represents a photo to be sent. Can be sent as part of a group of media.
 * https://core.telegram.org/bots/api#inputmediaphoto
 */
public data class InputMediaPhoto(
    @SerializedName(InputMediaFields.MEDIA) override val media: TelegramFile,
    @SerializedName(InputMediaFields.CAPTION) override val caption: String? = null,
    @SerializedName(InputMediaFields.PARSE_MODE) override val parseMode: String? = null
) : InputMedia(), GroupableMedia {
    @SerializedName(InputMediaFields.TYPE)
    override val type: String = InputMediaTypes.PHOTO
}

/**
 * Represents a video to be sent. Can be sent as part of a group of media.
 * https://core.telegram.org/bots/api#inputmediavideo
 */
public data class InputMediaVideo(
    @SerializedName(InputMediaFields.MEDIA) override val media: TelegramFile,
    @SerializedName(InputMediaFields.CAPTION) override val caption: String? = null,
    @SerializedName(InputMediaFields.PARSE_MODE) override val parseMode: String? = null,
    @SerializedName(InputMediaFields.THUMB) val thumb: TelegramFile.ByFile? = null,
    @SerializedName(InputMediaFields.WIDTH) val width: Int? = null,
    @SerializedName(InputMediaFields.HEIGHT) val height: Int? = null,
    @SerializedName(InputMediaFields.DURATION) val duration: Int? = null,
    @SerializedName(InputMediaFields.SUPPORTS_STREAMING) val supportsStreaming: Boolean? = null
) : InputMedia(), GroupableMedia {
    @SerializedName(InputMediaFields.TYPE)
    override val type: String = InputMediaTypes.VIDEO
}

/**
 * Represents an animation file (GIF or H.264/MPEG-4 AVC video without sound) to be sent.
 * https://core.telegram.org/bots/api#inputmediaanimation
 */
public data class InputMediaAnimation(
    @SerializedName(InputMediaFields.MEDIA) override val media: TelegramFile,
    @SerializedName(InputMediaFields.CAPTION) override val caption: String? = null,
    @SerializedName(InputMediaFields.PARSE_MODE) override val parseMode: String? = null,
    @SerializedName(InputMediaFields.THUMB) val thumb: TelegramFile.ByFile? = null,
    @SerializedName(InputMediaFields.WIDTH) val width: Int? = null,
    @SerializedName(InputMediaFields.HEIGHT) val height: Int? = null,
    @SerializedName(InputMediaFields.DURATION) val duration: Int? = null
) : InputMedia() {
    @SerializedName(InputMediaFields.TYPE)
    override val type: String = InputMediaTypes.ANIMATION
}

/**
 * Represents an audio file to be treated as music to be sent.
 * https://core.telegram.org/bots/api#inputmediaaudio
 */
public data class InputMediaAudio(
    @SerializedName(InputMediaFields.MEDIA) override val media: TelegramFile,
    @SerializedName(InputMediaFields.CAPTION) override val caption: String? = null,
    @SerializedName(InputMediaFields.PARSE_MODE) override val parseMode: String? = null,
    @SerializedName(InputMediaFields.THUMB) val thumb: TelegramFile.ByFile? = null,
    @SerializedName(InputMediaFields.DURATION) val duration: Int? = null,
    @SerializedName(InputMediaFields.PERFORMER) val performer: String? = null,
    @SerializedName(InputMediaFields.TITLE) val title: String? = null
) : InputMedia(), GroupableMedia {
    @SerializedName(InputMediaFields.TYPE)
    override val type: String = InputMediaTypes.AUDIO
}

/**
 * Represents a general file to be sent.
 * https://core.telegram.org/bots/api#inputmediadocument
 */
public data class InputMediaDocument(
    @SerializedName(InputMediaFields.MEDIA) override val media: TelegramFile,
    @SerializedName(InputMediaFields.CAPTION) override val caption: String? = null,
    @SerializedName(InputMediaFields.PARSE_MODE) override val parseMode: String? = null,
    @SerializedName(InputMediaFields.THUMB) val thumb: TelegramFile.ByFile? = null,
    @SerializedName(InputMediaFields.DISABLE_CONTENT_TYPE_DETECTION) val disableContentTypeDetection: Boolean? = null
) : InputMedia(), GroupableMedia {
    @SerializedName(InputMediaFields.TYPE)
    override val type: String = InputMediaTypes.DOCUMENT
}
