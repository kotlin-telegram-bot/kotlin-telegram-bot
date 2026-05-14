package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatBoostUpdatedTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes ChatBoostUpdated from JSON`() {
        val json = """
            {
              "chat":{"id":-1,"type":"channel"},
              "boost":{
                "boost_id":"b1",
                "add_date":1,
                "expiration_date":2,
                "source":{"source":"gift_code","user":{"id":1,"is_bot":false,"first_name":"Alice"}}
              }
            }
        """.trimIndent()

        val updated = gson.fromJson(json, ChatBoostUpdated::class.java)

        assertThat(updated.chat.id).isEqualTo(-1L)
        assertThat(updated.boost.boostId).isEqualTo("b1")
        assertThat(updated.boost.source).isInstanceOf(ChatBoostSource.GiftCode::class.java)
    }
}
