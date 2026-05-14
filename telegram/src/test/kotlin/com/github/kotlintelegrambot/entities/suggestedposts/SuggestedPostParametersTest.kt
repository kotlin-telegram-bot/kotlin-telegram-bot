package com.github.kotlintelegrambot.entities.suggestedposts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SuggestedPostParametersTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes SuggestedPostParameters with all fields`() {
        val json = """
            {"price":{"currency":"XTR","amount":100},"send_date":1700000000}
        """.trimIndent()

        val params = gson.fromJson(json, SuggestedPostParameters::class.java)

        assertThat(params.price?.currency).isEqualTo("XTR")
        assertThat(params.price?.amount).isEqualTo(100L)
        assertThat(params.sendDate).isEqualTo(1700000000L)
    }

    @Test
    fun `deserializes empty SuggestedPostParameters`() {
        val params = gson.fromJson("{}", SuggestedPostParameters::class.java)

        assertThat(params.price).isNull()
        assertThat(params.sendDate).isNull()
    }

    @Test
    fun `deserializes SuggestedPostPrice`() {
        val json = """{"currency":"TON","amount":2500}"""

        val price = gson.fromJson(json, SuggestedPostPrice::class.java)

        assertThat(price.currency).isEqualTo("TON")
        assertThat(price.amount).isEqualTo(2500L)
    }
}
