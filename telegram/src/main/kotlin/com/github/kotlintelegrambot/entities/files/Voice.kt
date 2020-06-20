package com.github.kotlintelegrambot.entities.files

import com.google.gson.annotations.SerializedName

/**
 * Represents a voice note.
 * https://core.telegram.org/bots/api#voice
 */
data class Voice(
    @SerializedName(FilesFields.fileId) val fileId: String,
    @SerializedName(FilesFields.fileUniqueId) val fileUniqueId: String,
    @SerializedName(FilesFields.duration) val duration: Int,
    @SerializedName(FilesFields.mimeType) val mimeType: String? = null,
    @SerializedName(FilesFields.fileSize) val fileSize: Int? = null
)
