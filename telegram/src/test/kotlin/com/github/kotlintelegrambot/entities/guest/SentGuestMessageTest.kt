package com.github.kotlintelegrambot.entities.guest

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SentGuestMessageTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes SentGuestMessage`() {
        val json = """
            {"chat":{"id":-100,"type":"supergroup"},"message_id":12345}
        """.trimIndent()

        val sent = gson.fromJson(json, SentGuestMessage::class.java)

        assertThat(sent.chat.id).isEqualTo(-100L)
        assertThat(sent.messageId).isEqualTo(12345L)
    }
}
