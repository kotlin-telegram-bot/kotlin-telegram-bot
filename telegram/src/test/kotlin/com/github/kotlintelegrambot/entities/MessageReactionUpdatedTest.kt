package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MessageReactionUpdatedTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes MessageReactionUpdated from JSON`() {
        val json =
            """
            {
              "chat":{"id":-100,"type":"supergroup"},
              "message_id":42,
              "user":{"id":7,"is_bot":false,"first_name":"Alice"},
              "date":1700000000,
              "old_reaction":[],
              "new_reaction":[]
            }
            """.trimIndent()

        val update = gson.fromJson(json, MessageReactionUpdated::class.java)

        assertThat(update.chat.id).isEqualTo(-100L)
        assertThat(update.messageId).isEqualTo(42L)
        assertThat(update.user?.firstName).isEqualTo("Alice")
        assertThat(update.actorChat).isNull()
        assertThat(update.date).isEqualTo(1700000000L)
        assertThat(update.oldReaction).isEmpty()
        assertThat(update.newReaction).isEmpty()
    }

    @Test
    fun `deserializes MessageReactionUpdated with actor_chat`() {
        val json =
            """
            {
              "chat":{"id":-1,"type":"channel"},
              "message_id":1,
              "actor_chat":{"id":-9,"type":"channel"},
              "date":1700000001,
              "old_reaction":[],
              "new_reaction":[]
            }
            """.trimIndent()

        val update = gson.fromJson(json, MessageReactionUpdated::class.java)

        assertThat(update.user).isNull()
        assertThat(update.actorChat?.id).isEqualTo(-9L)
    }
}
