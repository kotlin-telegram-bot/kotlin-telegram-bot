package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatBoostTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes ChatBoost from JSON`() {
        val json =
            """
            {
              "boost_id":"abc",
              "add_date":1700000000,
              "expiration_date":1800000000,
              "source":{"source":"premium","user":{"id":1,"is_bot":false,"first_name":"Alice"}}
            }
            """.trimIndent()

        val boost = gson.fromJson(json, ChatBoost::class.java)

        assertThat(boost.boostId).isEqualTo("abc")
        assertThat(boost.addDate).isEqualTo(1700000000L)
        assertThat(boost.expirationDate).isEqualTo(1800000000L)
        assertThat(boost.source).isInstanceOf(ChatBoostSource.Premium::class.java)
    }
}
