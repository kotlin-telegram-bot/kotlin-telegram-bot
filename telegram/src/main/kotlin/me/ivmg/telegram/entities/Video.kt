package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("file_id") val fileId: String,
    val width: Int,
    val height: Int,
    val duration: Int,
    val thumb: PhotoSize?,
    @SerializedName("mime_type") val mimeType: String?,
    @SerializedName("file_size") val fileSize: Int?
)
