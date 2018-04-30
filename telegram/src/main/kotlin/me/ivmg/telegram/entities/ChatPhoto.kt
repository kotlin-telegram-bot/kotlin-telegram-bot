package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class ChatPhoto(
    @Name("small_file_id") val smallFileId: String,
    @Name("big_file_id") val bigFileId: String
)