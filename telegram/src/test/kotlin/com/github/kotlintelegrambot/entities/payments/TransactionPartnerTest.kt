package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TransactionPartnerTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes TransactionPartnerUser by 'user' discriminator`() {
        val json = """
            {"type":"user",
             "user":{"id":42,"is_bot":false,"first_name":"Alice"},
             "invoice_payload":"sku-7",
             "subscription_period":2592000,
             "paid_media_payload":"hidden"}
        """.trimIndent()

        val partner = gson.fromJson(json, TransactionPartner::class.java)

        assertThat(partner).isInstanceOf(TransactionPartner.User::class.java)
        partner as TransactionPartner.User
        assertThat(partner.user.id).isEqualTo(42L)
        assertThat(partner.invoicePayload).isEqualTo("sku-7")
        assertThat(partner.subscriptionPeriod).isEqualTo(2592000)
        assertThat(partner.paidMediaPayload).isEqualTo("hidden")
    }

    @Test
    fun `deserializes TransactionPartnerFragment with withdrawal state`() {
        val json = """
            {"type":"fragment",
             "withdrawal_state":{"type":"succeeded","date":1700000000,"url":"https://fragment.com/tx/1"}}
        """.trimIndent()

        val partner = gson.fromJson(json, TransactionPartner::class.java)

        assertThat(partner).isInstanceOf(TransactionPartner.Fragment::class.java)
        partner as TransactionPartner.Fragment
        assertThat(partner.withdrawalState).isInstanceOf(RevenueWithdrawalState.Succeeded::class.java)
        val state = partner.withdrawalState as RevenueWithdrawalState.Succeeded
        assertThat(state.url).isEqualTo("https://fragment.com/tx/1")
    }

    @Test
    fun `deserializes TransactionPartnerTelegramAds`() {
        val json = """{"type":"telegram_ads"}"""

        val partner = gson.fromJson(json, TransactionPartner::class.java)

        assertThat(partner).isInstanceOf(TransactionPartner.TelegramAds::class.java)
    }

    @Test
    fun `deserializes TransactionPartnerTelegramApi with request count`() {
        val json = """{"type":"telegram_api","request_count":17}"""

        val partner = gson.fromJson(json, TransactionPartner::class.java)

        assertThat(partner).isInstanceOf(TransactionPartner.TelegramApi::class.java)
        partner as TransactionPartner.TelegramApi
        assertThat(partner.requestCount).isEqualTo(17)
    }

    @Test
    fun `deserializes TransactionPartnerAffiliateProgram`() {
        val json = """
            {"type":"affiliate_program",
             "sponsor_user":{"id":7,"is_bot":true,"first_name":"Sponsor"},
             "commission_per_mille":150}
        """.trimIndent()

        val partner = gson.fromJson(json, TransactionPartner::class.java)

        assertThat(partner).isInstanceOf(TransactionPartner.AffiliateProgram::class.java)
        partner as TransactionPartner.AffiliateProgram
        assertThat(partner.sponsorUser?.id).isEqualTo(7L)
        assertThat(partner.commissionPerMille).isEqualTo(150)
    }

    @Test
    fun `deserializes TransactionPartnerChat`() {
        val json = """{"type":"chat","chat":{"id":-100,"type":"supergroup"}}"""

        val partner = gson.fromJson(json, TransactionPartner::class.java)

        assertThat(partner).isInstanceOf(TransactionPartner.Chat::class.java)
        partner as TransactionPartner.Chat
        assertThat(partner.chat.id).isEqualTo(-100L)
    }

    @Test
    fun `deserializes TransactionPartnerOther`() {
        val json = """{"type":"other"}"""

        val partner = gson.fromJson(json, TransactionPartner::class.java)

        assertThat(partner).isInstanceOf(TransactionPartner.Other::class.java)
    }
}
