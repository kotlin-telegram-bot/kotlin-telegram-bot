package com.github.kotlintelegrambot.entities.inputmedia

import com.google.gson.annotations.SerializedName as Name

data class InputMediaDocument(
    override val type: String,
    override val media: String,
    override val caption: String? = null,
    @Name("parse_mode") val parseMode: String? = null
) : InputMedia()
