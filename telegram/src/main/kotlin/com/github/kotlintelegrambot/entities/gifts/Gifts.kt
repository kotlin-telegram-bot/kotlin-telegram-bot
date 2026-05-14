package com.github.kotlintelegrambot.entities.gifts

import com.google.gson.annotations.SerializedName

/**
 * Represents a list of gifts. (Bot API 8.0)
 *
 * See https://core.telegram.org/bots/api#gifts
 */
data class Gifts(
    @SerializedName("gifts") val gifts: List<Gift>,
)
