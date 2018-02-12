package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

enum class DataType(val dataType: String) {
    DOCUMENT("document"),
    STICKER("sticker")
}

data class InputFile(
    @Name("chat_id") val chatId: String,
    val photo: String?,
    val audio: String?,
    @Name("data_type") val dataType: String,
    val video: String?,
    val voice: String?,
    @Name("video_note") val videoNote: String
)