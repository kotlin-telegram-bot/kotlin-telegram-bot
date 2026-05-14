package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatBoostAddedTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes boost_count`() {
        val obj = gson.fromJson("""{"boost_count":3}""", ChatBoostAdded::class.java)
        assertThat(obj.boostCount).isEqualTo(3)
    }
}
