package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class Document(
    @Name("file_id") val fileId: String,
    val thumb: PhotoSize?,
    @Name("file_name") val fileName: String?,
    @Name("mime_type") val mimeType: String?,
    @Name("file_size") val fileSize: Int?
)