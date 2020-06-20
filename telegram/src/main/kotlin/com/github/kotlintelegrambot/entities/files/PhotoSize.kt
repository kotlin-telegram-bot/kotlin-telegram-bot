package com.github.kotlintelegrambot.entities.files

import com.google.gson.annotations.SerializedName

/**
 * Represents one size of a photo or a file / sticker thumbnail.
 * https://core.telegram.org/bots/api#photosize
 */
data class PhotoSize(
    @SerializedName(FilesFields.fileId) val fileId: String,
    @SerializedName(FilesFields.fileUniqueId) val fileUniqueId: String,
    @SerializedName(FilesFields.width) val width: Int,
    @SerializedName(FilesFields.height) val height: Int,
    @SerializedName(FilesFields.fileSize) val fileSize: Int? = null
)
