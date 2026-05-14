package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AffiliateInfoTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes AffiliateInfo payload`() {
        val json = """
            {"affiliate_user":{"id":1,"is_bot":false,"first_name":"Affy"},
             "affiliate_chat":{"id":-100,"type":"supergroup"},
             "commission_per_mille":250,
             "amount":1000,
             "nanostar_amount":500000000}
        """.trimIndent()

        val info = gson.fromJson(json, AffiliateInfo::class.java)

        assertThat(info.affiliateUser?.id).isEqualTo(1L)
        assertThat(info.affiliateChat?.id).isEqualTo(-100L)
        assertThat(info.commissionPerMille).isEqualTo(250)
        assertThat(info.amount).isEqualTo(1000)
        assertThat(info.nanostarAmount).isEqualTo(500000000)
    }
}
