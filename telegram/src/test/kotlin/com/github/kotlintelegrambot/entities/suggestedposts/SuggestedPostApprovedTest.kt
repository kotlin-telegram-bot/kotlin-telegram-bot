package com.github.kotlintelegrambot.entities.suggestedposts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SuggestedPostApprovedTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes SuggestedPostApproved`() {
        val json = """
            {"price":{"currency":"XTR","amount":50},"send_date":1700000000}
        """.trimIndent()

        val approved = gson.fromJson(json, SuggestedPostApproved::class.java)

        assertThat(approved.price?.currency).isEqualTo("XTR")
        assertThat(approved.sendDate).isEqualTo(1700000000L)
        assertThat(approved.suggestedPostMessage).isNull()
    }
}
