package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserChatBoostsTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes UserChatBoosts from JSON`() {
        val json = """
            {
              "boosts":[
                {
                  "boost_id":"b1",
                  "add_date":1,
                  "expiration_date":2,
                  "source":{"source":"premium","user":{"id":1,"is_bot":false,"first_name":"Alice"}}
                }
              ]
            }
        """.trimIndent()

        val boosts = gson.fromJson(json, UserChatBoosts::class.java)

        assertThat(boosts.boosts).hasSize(1)
        assertThat(boosts.boosts[0].boostId).isEqualTo("b1")
        assertThat(boosts.boosts[0].source).isInstanceOf(ChatBoostSource.Premium::class.java)
    }
}
