package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MaybeInaccessibleMessageTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes InaccessibleMessage with date 0`() {
        val json = """{"chat":{"id":1,"type":"private"},"message_id":7,"date":0}"""

        val msg = gson.fromJson(json, InaccessibleMessage::class.java)

        assertThat(msg.chat.id).isEqualTo(1L)
        assertThat(msg.messageId).isEqualTo(7L)
        assertThat(msg.date).isEqualTo(0L)
    }
}
