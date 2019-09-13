package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("file_id") val fileId: String,
    val width: Int,
    val height: Int,
    val duration: Int,
    val thumb: PhotoSize? = null,
    @SerializedName("mime_type") val mimeType: String? = null,
    @SerializedName("file_size") val fileSize: Int? = null
)
