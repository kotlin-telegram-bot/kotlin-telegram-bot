package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class Audio(
    @Name("file_id") val fileId: String,
    val duration: Int,
    val performer: String?,
    val title: String?,
    @Name("mime_type") val mimeType: String?,
    @Name("file_size") val fileSize: Int?
)