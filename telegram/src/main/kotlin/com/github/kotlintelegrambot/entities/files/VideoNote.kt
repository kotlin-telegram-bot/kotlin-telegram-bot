package com.github.kotlintelegrambot.entities.files

import com.google.gson.annotations.SerializedName

/**
 * Represents a video message (available in Telegram apps as of v.4.0).
 * https://core.telegram.org/bots/api#videonote
 */
data class VideoNote(
    @SerializedName(FilesFields.fileId) val fileId: String,
    @SerializedName(FilesFields.fileUniqueId) val fileUniqueId: String,
    @SerializedName(FilesFields.length) val length: Int,
    @SerializedName(FilesFields.duration) val duration: Int,
    @SerializedName(FilesFields.thumb) val thumb: PhotoSize? = null,
    @SerializedName(FilesFields.fileSize) val fileSize: Int? = null
)
