package me.ivmg.telegram.entities.inputmedia

data class InputMediaPhoto(
    override val type: String,
    override val media: String,
    override val caption: String?
) : InputMedia()
