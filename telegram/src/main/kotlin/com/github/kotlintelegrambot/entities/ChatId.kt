package com.github.kotlintelegrambot.entities

/**
 * Unique identifier for the target chat or username of the target channel (in the format @channelusername)
 */
sealed class ChatId {
    data class Id(val id: Long) : ChatId()
    class Username(username: String) : ChatId() {
        val username: String = if (username.startsWith("@")) username else "@$username"
    }

    companion object {
        fun fromId(id: Long) = Id(id)
        fun fromUsername(username: String) = Username(username)
    }
}
