package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class Document(
    @Name("file_id") val fileId: String,
    val thumb: PhotoSize? = null,
    @Name("file_name") val fileName: String? = null,
    @Name("mime_type") val mimeType: String? = null,
    @Name("file_size") val fileSize: Int? = null
)
