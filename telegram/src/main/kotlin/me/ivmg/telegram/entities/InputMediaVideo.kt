package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class InputMediaVideo(
    override val type: String,
    override val media: String,
    override val caption: String?,
    val width: Int?,
    val height: Int?,
    val duration: Int?
) : InputMedia()