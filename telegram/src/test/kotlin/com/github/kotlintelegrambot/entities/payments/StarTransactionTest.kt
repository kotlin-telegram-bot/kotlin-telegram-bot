package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StarTransactionTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes StarTransaction with source partner`() {
        val json = """
            {"id":"tx-1",
             "amount":100,
             "nanostar_amount":500000000,
             "date":1700000000,
             "source":{"type":"user",
                       "user":{"id":42,"is_bot":false,"first_name":"Alice"},
                       "invoice_payload":"sku-7"}}
        """.trimIndent()

        val tx = gson.fromJson(json, StarTransaction::class.java)

        assertThat(tx.id).isEqualTo("tx-1")
        assertThat(tx.amount).isEqualTo(100)
        assertThat(tx.nanostarAmount).isEqualTo(500000000)
        assertThat(tx.date).isEqualTo(1700000000L)
        assertThat(tx.source).isInstanceOf(TransactionPartner.User::class.java)
        assertThat(tx.receiver).isNull()
    }

    @Test
    fun `deserializes StarTransaction with receiver partner`() {
        val json = """
            {"id":"tx-2",
             "amount":50,
             "date":1700000001,
             "receiver":{"type":"fragment"}}
        """.trimIndent()

        val tx = gson.fromJson(json, StarTransaction::class.java)

        assertThat(tx.id).isEqualTo("tx-2")
        assertThat(tx.receiver).isInstanceOf(TransactionPartner.Fragment::class.java)
        assertThat(tx.source).isNull()
    }
}
