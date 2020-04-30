package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

data class VideoNote(
    @SerializedName("file_id") val fileId: String,
    val length: Int,
    val duration: Int,
    val thumb: PhotoSize? = null,
    @SerializedName("file_size") val fileSize: Int? = null
)
