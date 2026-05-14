package com.github.kotlintelegrambot.entities.inputmedia

import com.github.kotlintelegrambot.entities.MessageEntity
import com.github.kotlintelegrambot.entities.ParseMode
import com.google.gson.annotations.SerializedName

/**
 * Represents a live photo to be sent. (Bot API 10.0)
 *
 * Standalone definition: not yet wired into the existing [InputMedia] sealed type. See follow-up
 * notes in the PR description.
 *
 * See https://core.telegram.org/bots/api#inputmedialivephoto
 */
data class InputMediaLivePhoto(
    @SerializedName("type") val type: String = "live_photo",
    @SerializedName("video") val video: String,
    @SerializedName("photo") val photo: String,
    @SerializedName("caption") val caption: String? = null,
    @SerializedName("parse_mode") val parseMode: ParseMode? = null,
    @SerializedName("caption_entities") val captionEntities: List<MessageEntity>? = null,
    @SerializedName("show_caption_above_media") val showCaptionAboveMedia: Boolean? = null,
)
