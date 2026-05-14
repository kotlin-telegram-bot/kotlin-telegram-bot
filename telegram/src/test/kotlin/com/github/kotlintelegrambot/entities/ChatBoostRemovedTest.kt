package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatBoostRemovedTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes ChatBoostRemoved from JSON`() {
        val json =
            """
            {
              "chat":{"id":-1,"type":"channel"},
              "boost_id":"b1",
              "remove_date":1700000000,
              "source":{"source":"giveaway","giveaway_message_id":99}
            }
            """.trimIndent()

        val removed = gson.fromJson(json, ChatBoostRemoved::class.java)

        assertThat(removed.chat.id).isEqualTo(-1L)
        assertThat(removed.boostId).isEqualTo("b1")
        assertThat(removed.removeDate).isEqualTo(1700000000L)
        assertThat(removed.source).isInstanceOf(ChatBoostSource.Giveaway::class.java)
        val gv = removed.source as ChatBoostSource.Giveaway
        assertThat(gv.giveawayMessageId).isEqualTo(99L)
    }
}
