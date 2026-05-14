package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StarAmountTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes StarAmount with both fields`() {
        val json = """{"amount":42,"nanostar_amount":500000000}"""

        val starAmount = gson.fromJson(json, StarAmount::class.java)

        assertThat(starAmount.amount).isEqualTo(42)
        assertThat(starAmount.nanostarAmount).isEqualTo(500000000)
    }

    @Test
    fun `deserializes StarAmount without nanostar amount`() {
        val json = """{"amount":-3}"""

        val starAmount = gson.fromJson(json, StarAmount::class.java)

        assertThat(starAmount.amount).isEqualTo(-3)
        assertThat(starAmount.nanostarAmount).isNull()
    }
}
