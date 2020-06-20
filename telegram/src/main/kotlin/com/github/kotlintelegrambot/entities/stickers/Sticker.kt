package com.github.kotlintelegrambot.entities.stickers

import com.github.kotlintelegrambot.entities.files.FilesFields
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.google.gson.annotations.SerializedName

/**
 * Represents a sticker.
 * https://core.telegram.org/bots/api#sticker
 */
data class Sticker(
    @SerializedName(FilesFields.fileId) val fileId: String,
    @SerializedName(FilesFields.fileUniqueId) val fileUniqueId: String,
    @SerializedName(FilesFields.width) val width: Int,
    @SerializedName(FilesFields.height) val height: Int,
    @SerializedName(FilesFields.isAnimated) val isAnimated: Boolean,
    @SerializedName(FilesFields.thumb) val thumb: PhotoSize? = null,
    @SerializedName(FilesFields.emoji) val emoji: String?,
    @SerializedName(FilesFields.setName)val setName: String? = null,
    @SerializedName(FilesFields.maskPosition) val maskPosition: MaskPosition? = null,
    @SerializedName(FilesFields.fileSize) val fileSize: Int? = null
)
