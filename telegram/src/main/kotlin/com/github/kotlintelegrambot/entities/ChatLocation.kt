package com.github.kotlintelegrambot.entities

/**
 * Represents a location to which a chat is connected.
 * https://core.telegram.org/bots/api#chatlocation
 */
data class ChatLocation(
    val location: Location,
    val address: String,
)
