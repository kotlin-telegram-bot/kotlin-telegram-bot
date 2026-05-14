package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DirectMessagePriceChangedTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes DirectMessagePriceChanged with star count`() {
        val json = """{"are_direct_messages_enabled":true,"direct_message_star_count":50}"""

        val changed = gson.fromJson(json, DirectMessagePriceChanged::class.java)

        assertThat(changed.areDirectMessagesEnabled).isTrue
        assertThat(changed.directMessageStarCount).isEqualTo(50)
    }

    @Test
    fun `deserializes DirectMessagePriceChanged disabled`() {
        val json = """{"are_direct_messages_enabled":false}"""

        val changed = gson.fromJson(json, DirectMessagePriceChanged::class.java)

        assertThat(changed.areDirectMessagesEnabled).isFalse
        assertThat(changed.directMessageStarCount).isNull()
    }
}
