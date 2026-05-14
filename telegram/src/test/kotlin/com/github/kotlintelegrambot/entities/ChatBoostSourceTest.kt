package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatBoostSourceTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes ChatBoostSourcePremium by 'premium' discriminator`() {
        val json = """{"source":"premium","user":{"id":1,"is_bot":false,"first_name":"Alice"}}"""

        val src = gson.fromJson(json, ChatBoostSource::class.java)

        assertThat(src).isInstanceOf(ChatBoostSource.Premium::class.java)
        src as ChatBoostSource.Premium
        assertThat(src.user.firstName).isEqualTo("Alice")
    }

    @Test
    fun `deserializes ChatBoostSourceGiftCode by 'gift_code' discriminator`() {
        val json = """{"source":"gift_code","user":{"id":2,"is_bot":false,"first_name":"Bob"}}"""

        val src = gson.fromJson(json, ChatBoostSource::class.java)

        assertThat(src).isInstanceOf(ChatBoostSource.GiftCode::class.java)
        src as ChatBoostSource.GiftCode
        assertThat(src.user.firstName).isEqualTo("Bob")
    }

    @Test
    fun `deserializes ChatBoostSourceGiveaway by 'giveaway' discriminator`() {
        val json =
            """
            {"source":"giveaway","giveaway_message_id":77,"user":{"id":3,"is_bot":false,"first_name":"Carol"},"prize_star_count":50,"is_unclaimed":true}
            """.trimIndent()

        val src = gson.fromJson(json, ChatBoostSource::class.java)

        assertThat(src).isInstanceOf(ChatBoostSource.Giveaway::class.java)
        src as ChatBoostSource.Giveaway
        assertThat(src.giveawayMessageId).isEqualTo(77L)
        assertThat(src.user?.firstName).isEqualTo("Carol")
        assertThat(src.prizeStarCount).isEqualTo(50)
        assertThat(src.isUnclaimed).isTrue()
    }

    @Test
    fun `deserializes ChatBoostSourceGiveaway with unclaimed prize and no user`() {
        val json = """{"source":"giveaway","giveaway_message_id":8}"""

        val src = gson.fromJson(json, ChatBoostSource::class.java)

        assertThat(src).isInstanceOf(ChatBoostSource.Giveaway::class.java)
        src as ChatBoostSource.Giveaway
        assertThat(src.giveawayMessageId).isEqualTo(8L)
        assertThat(src.user).isNull()
        assertThat(src.prizeStarCount).isNull()
        assertThat(src.isUnclaimed).isNull()
    }
}
