package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PreparedInlineMessageTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes PreparedInlineMessage`() {
        val json = """{"id":"prep_id","expiration_date":1735689600}"""

        val msg = gson.fromJson(json, PreparedInlineMessage::class.java)

        assertThat(msg.id).isEqualTo("prep_id")
        assertThat(msg.expirationDate).isEqualTo(1735689600L)
    }
}
