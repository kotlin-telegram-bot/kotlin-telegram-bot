package com.github.kotlintelegrambot.entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ChatIdTest {
    @Test
    fun `should not add @ to channel username if already present`() {
        val username = "@testy"
        val chatId = ChatId.fromChannelUsername(username)
        assertEquals(username, chatId.username)
    }

    @Test
    fun `should add @ to channel username if not present`() {
        val username = "testy"
        val chatId = ChatId.fromChannelUsername(username)
        assertEquals("@$username", chatId.username)
    }
}
