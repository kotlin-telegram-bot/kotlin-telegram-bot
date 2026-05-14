package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GiftInfoTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes GiftInfo with optional fields`() {
        val stickerJson =
            """{"file_id":"f","file_unique_id":"u","width":1,"height":1,"is_animated":false,"emoji":"🎁"}"""
        val json =
            """
            {
              "gift":{"id":"g","sticker":$stickerJson,"star_count":10},
              "owned_gift_id":"og1",
              "convert_star_count":5,
              "prepaid_upgrade_star_count":2,
              "can_be_upgraded":true,
              "text":"hi",
              "is_private":false
            }
            """.trimIndent()

        val info = gson.fromJson(json, GiftInfo::class.java)

        assertThat(info.gift.id).isEqualTo("g")
        assertThat(info.ownedGiftId).isEqualTo("og1")
        assertThat(info.convertStarCount).isEqualTo(5)
        assertThat(info.prepaidUpgradeStarCount).isEqualTo(2)
        assertThat(info.canBeUpgraded).isTrue()
        assertThat(info.text).isEqualTo("hi")
        assertThat(info.isPrivate).isFalse()
    }
}
