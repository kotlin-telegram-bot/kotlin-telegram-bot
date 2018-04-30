package me.ivmg.telegram.entities

data class InputMediaVideo(
    override val type: String,
    override val media: String,
    override val caption: String?,
    val width: Int?,
    val height: Int?,
    val duration: Int?
) : InputMedia()