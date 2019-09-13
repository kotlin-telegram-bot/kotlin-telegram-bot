package me.ivmg.telegram.entities.inputmedia

import com.google.gson.annotations.SerializedName as Name

data class InputMediaVideo(
    override val type: String,
    override val media: String,
    override val caption: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Int? = null,
    @Name("supports_streaming") val supportsStreaming: Boolean? = null
) : InputMedia()
