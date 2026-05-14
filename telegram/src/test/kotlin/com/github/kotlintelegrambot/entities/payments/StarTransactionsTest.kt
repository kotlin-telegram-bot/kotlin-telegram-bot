package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StarTransactionsTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes StarTransactions list payload`() {
        val json = """
            {"transactions":[
              {"id":"tx-1","amount":10,"date":1700000000,"source":{"type":"other"}},
              {"id":"tx-2","amount":20,"date":1700000001,"receiver":{"type":"telegram_ads"}}
            ]}
        """.trimIndent()

        val txs = gson.fromJson(json, StarTransactions::class.java)

        assertThat(txs.transactions).hasSize(2)
        assertThat(txs.transactions[0].id).isEqualTo("tx-1")
        assertThat(txs.transactions[0].source).isInstanceOf(TransactionPartner.Other::class.java)
        assertThat(txs.transactions[1].receiver).isInstanceOf(TransactionPartner.TelegramAds::class.java)
    }
}
