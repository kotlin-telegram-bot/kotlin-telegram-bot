package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PaidMediaPurchasedTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes PaidMediaPurchased payload`() {
        val json = """
            {"from":{"id":42,"is_bot":false,"first_name":"Alice"},
             "paid_media_payload":"sku-7"}
        """.trimIndent()

        val purchased = gson.fromJson(json, PaidMediaPurchased::class.java)

        assertThat(purchased.from.firstName).isEqualTo("Alice")
        assertThat(purchased.paidMediaPayload).isEqualTo("sku-7")
    }
}
