package com.github.kotlintelegrambot.entities.files

import com.google.gson.annotations.SerializedName

/**
 * Represents an audio file to be treated as music by the Telegram clients.
 * https://core.telegram.org/bots/api#audio
 */
data class Audio(
    @SerializedName(FilesFields.fileId) val fileId: String,
    @SerializedName(FilesFields.fileUniqueId) val fileUniqueId: String,
    @SerializedName(FilesFields.duration) val duration: Int,
    @SerializedName(FilesFields.performer) val performer: String? = null,
    @SerializedName(FilesFields.title) val title: String? = null,
    @SerializedName(FilesFields.mimeType) val mimeType: String? = null,
    @SerializedName(FilesFields.fileSize) val fileSize: Int? = null,
    @SerializedName(FilesFields.thumb) val thumb: PhotoSize? = null,
    @SerializedName(FilesFields.fileName) val fileName: String? = null
)
