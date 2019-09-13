package me.ivmg.telegram.entities.inputmedia

import com.google.gson.annotations.SerializedName as Name

data class InputMediaPhoto(
    override val type: String,
    override val media: String,
    override val caption: String?,
    @Name("parse_mode") val parseMode: String?
) : InputMedia()
