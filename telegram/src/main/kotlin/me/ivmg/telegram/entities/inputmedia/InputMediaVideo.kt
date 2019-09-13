package me.ivmg.telegram.entities.inputmedia

import com.google.gson.annotations.SerializedName as Name

data class InputMediaVideo(
    override val type: String,
    override val media: String,
    override val caption: String?,
    val width: Int?,
    val height: Int?,
    val duration: Int?,
    @Name("supports_streaming") val supportsStreaming: Boolean?
) : InputMedia()
