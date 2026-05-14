package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatFullInfoTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes minimal ChatFullInfo`() {
        val json = """{"id":42,"type":"private","first_name":"Alice","accent_color_id":3,"max_reaction_count":5}"""

        val info = gson.fromJson(json, ChatFullInfo::class.java)

        assertThat(info.id).isEqualTo(42L)
        assertThat(info.type).isEqualTo("private")
        assertThat(info.firstName).isEqualTo("Alice")
        assertThat(info.accentColorId).isEqualTo(3)
        assertThat(info.maxReactionCount).isEqualTo(5)
    }

    @Test
    fun `deserializes ChatFullInfo with birthdate and business fields`() {
        val json = """
            {
              "id": -100,
              "type": "private",
              "first_name": "Bob",
              "accent_color_id": 1,
              "max_reaction_count": 10,
              "birthdate": {"day": 7, "month": 3, "year": 1985},
              "business_intro": {"title": "Welcome"},
              "has_private_forwards": true,
              "unrestrict_boost_count": 5,
              "custom_emoji_sticker_set_name": "CustomEmojiSet"
            }
        """.trimIndent()

        val info = gson.fromJson(json, ChatFullInfo::class.java)

        assertThat(info.id).isEqualTo(-100L)
        assertThat(info.birthdate?.day).isEqualTo(7)
        assertThat(info.birthdate?.month).isEqualTo(3)
        assertThat(info.birthdate?.year).isEqualTo(1985)
        assertThat(info.businessIntro?.title).isEqualTo("Welcome")
        assertThat(info.hasPrivateForwards).isTrue()
        assertThat(info.unrestrictBoostCount).isEqualTo(5)
        assertThat(info.customEmojiStickerSetName).isEqualTo("CustomEmojiSet")
    }

    @Test
    fun `deserializes ChatFullInfo with permissions and location`() {
        val json = """
            {
              "id": 5,
              "type": "supergroup",
              "title": "Group",
              "permissions": {"can_send_messages": true},
              "location": {"location": {"longitude": 1.0, "latitude": 2.0}, "address": "addr"},
              "can_send_paid_media": true
            }
        """.trimIndent()

        val info = gson.fromJson(json, ChatFullInfo::class.java)

        assertThat(info.title).isEqualTo("Group")
        assertThat(info.permissions?.canSendMessages).isTrue()
        assertThat(info.location?.address).isEqualTo("addr")
        assertThat(info.canSendPaidMedia).isTrue()
    }
}
