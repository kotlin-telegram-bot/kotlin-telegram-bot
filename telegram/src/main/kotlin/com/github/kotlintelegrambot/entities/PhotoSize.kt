package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class PhotoSize(
    @Name("file_id") val fileId: String,
    val width: Int,
    val height: Int,
    @Name("file_size") val fileSize: Int? = null
)
