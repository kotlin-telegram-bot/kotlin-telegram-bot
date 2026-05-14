package com.github.kotlintelegrambot.entities.suggestedposts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SuggestedPostApprovalFailedTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes SuggestedPostApprovalFailed with price`() {
        val json = """{"price":{"currency":"XTR","amount":99}}"""

        val failed = gson.fromJson(json, SuggestedPostApprovalFailed::class.java)

        assertThat(failed.price?.amount).isEqualTo(99L)
        assertThat(failed.suggestedPostMessage).isNull()
    }

    @Test
    fun `deserializes empty SuggestedPostApprovalFailed`() {
        val failed = gson.fromJson("{}", SuggestedPostApprovalFailed::class.java)

        assertThat(failed.price).isNull()
        assertThat(failed.suggestedPostMessage).isNull()
    }
}
