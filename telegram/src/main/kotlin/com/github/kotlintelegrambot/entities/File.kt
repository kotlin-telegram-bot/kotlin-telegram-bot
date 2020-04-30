package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class File(
    @Name("file_id") val fileId: String,
    @Name("file_size") val fileSize: Int? = null,
    @Name("file_path") val filePath: String? = null
)
