package com.github.kotlintelegrambot.entities

/**
 * Unique identifier for the target chat or username of the target channel (in the format @channelusername)
 */
public sealed class ChatId {
    public data class Id(val id: Long) : ChatId()

    public class ChannelUsername(username: String) : ChatId() {
        public val username: String = if (username.startsWith("@")) username else "@$username"
    }

    public companion object {
        public fun fromId(id: Long): Id = Id(id)
        public fun fromChannelUsername(username: String): ChannelUsername = ChannelUsername(username)
    }
}
