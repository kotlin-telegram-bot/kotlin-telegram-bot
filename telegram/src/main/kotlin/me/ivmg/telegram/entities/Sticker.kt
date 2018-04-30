package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class Sticker(
    @Name("file_id") val fileId: String,
    val width: Int,
    val height: Int,
    val thumb: PhotoSize?,
    val emoji: String?,
    @Name("file_size") val fileSize: Int?
)