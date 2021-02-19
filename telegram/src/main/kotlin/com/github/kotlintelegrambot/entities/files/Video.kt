package com.github.kotlintelegrambot.entities.files

import com.google.gson.annotations.SerializedName

/**
 * Represents a video file.
 * https://core.telegram.org/bots/api#video
 */
data class Video(
    @SerializedName(FilesFields.fileId) val fileId: String,
    @SerializedName(FilesFields.fileUniqueId) val fileUniqueId: String,
    @SerializedName(FilesFields.width) val width: Int,
    @SerializedName(FilesFields.height) val height: Int,
    @SerializedName(FilesFields.duration) val duration: Int,
    @SerializedName(FilesFields.thumb) val thumb: PhotoSize? = null,
    @SerializedName(FilesFields.mimeType) val mimeType: String? = null,
    @SerializedName(FilesFields.fileSize) val fileSize: Int? = null,
    @SerializedName(FilesFields.fileName) val fileName: String? = null
)
