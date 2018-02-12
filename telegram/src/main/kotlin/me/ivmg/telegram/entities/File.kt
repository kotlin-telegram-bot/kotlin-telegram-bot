package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class File(
    @Name("file_id") val fileId: String,
    @Name("file_size") val fileSize: Int?,
    @Name("file_path") val filePath: String?
)