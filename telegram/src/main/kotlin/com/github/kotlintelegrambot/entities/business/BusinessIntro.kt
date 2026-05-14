package com.github.kotlintelegrambot.entities.business

import com.github.kotlintelegrambot.entities.stickers.Sticker
import com.google.gson.annotations.SerializedName as Name

/**
 * Describes the intro of a business account.
 *
 * https://core.telegram.org/bots/api#businessintro (Bot API 7.2)
 */
data class BusinessIntro(
    @Name("title") val title: String? = null,
    @Name("message") val message: String? = null,
    @Name("sticker") val sticker: Sticker? = null,
)
