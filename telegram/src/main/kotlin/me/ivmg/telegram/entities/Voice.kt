package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class Voice(
    @Name("file_id") val fileId: String,
    val duration: Int,
    @Name("mime_type") val mimeType: String?,
    @Name("file_size") val fileSize: Int?
)