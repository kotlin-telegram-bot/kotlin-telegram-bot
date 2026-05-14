package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RevenueWithdrawalStateTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes pending state`() {
        val json = """{"type":"pending"}"""

        val state = gson.fromJson(json, RevenueWithdrawalState.Pending::class.java)

        assertThat(state.type).isEqualTo("pending")
    }

    @Test
    fun `deserializes succeeded state with date and url`() {
        val json = """{"type":"succeeded","date":1700000000,"url":"https://fragment.com/tx/1"}"""

        val state = gson.fromJson(json, RevenueWithdrawalState.Succeeded::class.java)

        assertThat(state.type).isEqualTo("succeeded")
        assertThat(state.date).isEqualTo(1700000000L)
        assertThat(state.url).isEqualTo("https://fragment.com/tx/1")
    }

    @Test
    fun `deserializes failed state`() {
        val json = """{"type":"failed"}"""

        val state = gson.fromJson(json, RevenueWithdrawalState.Failed::class.java)

        assertThat(state.type).isEqualTo("failed")
    }
}
