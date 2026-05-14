package com.github.kotlintelegrambot.entities.suggestedposts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SuggestedPostInfoTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes SuggestedPostInfo with all fields`() {
        val json = """
            {"state":"pending","price":{"currency":"XTR","amount":10},"send_date":1700000000}
        """.trimIndent()

        val info = gson.fromJson(json, SuggestedPostInfo::class.java)

        assertThat(info.state).isEqualTo("pending")
        assertThat(info.price?.amount).isEqualTo(10L)
        assertThat(info.sendDate).isEqualTo(1700000000L)
    }

    @Test
    fun `deserializes minimal SuggestedPostInfo`() {
        val json = """{"state":"declined"}"""

        val info = gson.fromJson(json, SuggestedPostInfo::class.java)

        assertThat(info.state).isEqualTo("declined")
        assertThat(info.price).isNull()
        assertThat(info.sendDate).isNull()
    }
}
