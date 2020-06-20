package com.github.kotlintelegrambot.entities.files

import com.google.gson.annotations.SerializedName

/**
 * Represents a general file (as opposed to photos, voice messages and audio files).
 * https://core.telegram.org/bots/api#document
 */
data class Document(
    @SerializedName(FilesFields.fileId) val fileId: String,
    @SerializedName(FilesFields.fileUniqueId) val fileUniqueId: String,
    @SerializedName(FilesFields.thumb) val thumb: PhotoSize? = null,
    @SerializedName(FilesFields.fileName) val fileName: String? = null,
    @SerializedName(FilesFields.mimeType) val mimeType: String? = null,
    @SerializedName(FilesFields.fileSize) val fileSize: Int? = null
)
