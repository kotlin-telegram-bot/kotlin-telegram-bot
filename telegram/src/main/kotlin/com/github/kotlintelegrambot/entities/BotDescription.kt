package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

/**
 * Represents the bot's description. https://core.telegram.org/bots/api#botdescription (Bot API 6.7)
 */
data class BotDescription(
    @Name("description") val description: String,
)

/**
 * Represents the bot's short description. https://core.telegram.org/bots/api#botshortdescription (Bot API 6.7)
 */
data class BotShortDescription(
    @Name("short_description") val shortDescription: String,
)

/**
 * Represents the bot's name. https://core.telegram.org/bots/api#botname (Bot API 6.7)
 */
data class BotName(
    @Name("name") val name: String,
)
