package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AcceptedGiftTypesTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes AcceptedGiftTypes`() {
        val json = """
            {
              "unlimited_gifts":true,
              "limited_gifts":false,
              "unique_gifts":true,
              "premium_subscription":false
            }
        """.trimIndent()

        val types = gson.fromJson(json, AcceptedGiftTypes::class.java)

        assertThat(types.unlimitedGifts).isTrue()
        assertThat(types.limitedGifts).isFalse()
        assertThat(types.uniqueGifts).isTrue()
        assertThat(types.premiumSubscription).isFalse()
    }
}
