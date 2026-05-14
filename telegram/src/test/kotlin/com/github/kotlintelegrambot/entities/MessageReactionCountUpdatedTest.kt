package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MessageReactionCountUpdatedTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes MessageReactionCountUpdated from JSON with empty reactions`() {
        val json = """
            {
              "chat":{"id":-1,"type":"supergroup"},
              "message_id":11,
              "date":1700000000,
              "reactions":[]
            }
        """.trimIndent()

        val update = gson.fromJson(json, MessageReactionCountUpdated::class.java)

        assertThat(update.chat.id).isEqualTo(-1L)
        assertThat(update.messageId).isEqualTo(11L)
        assertThat(update.date).isEqualTo(1700000000L)
        assertThat(update.reactions).isEmpty()
    }
}
