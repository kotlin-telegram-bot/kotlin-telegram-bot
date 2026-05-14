package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UniqueGiftInfoTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes UniqueGiftInfo`() {
        val stickerJson =
            """{"file_id":"f","file_unique_id":"u","width":1,"height":1,"is_animated":false,"emoji":"🎁"}"""
        val json =
            """
            {
              "gift":{
                "base_name":"Dragon","name":"Dragon-7","number":7,
                "model":{"name":"M","sticker":$stickerJson,"rarity_per_mille":1},
                "symbol":{"name":"S","sticker":$stickerJson,"rarity_per_mille":2},
                "backdrop":{"name":"B","colors":{"center_color":1,"edge_color":2,"symbol_color":3,"text_color":4},"rarity_per_mille":3}
              },
              "origin":"upgrade",
              "owned_gift_id":"og1",
              "transfer_star_count":100,
              "next_transfer_date":1700001000
            }
            """.trimIndent()

        val info = gson.fromJson(json, UniqueGiftInfo::class.java)

        assertThat(info.gift.baseName).isEqualTo("Dragon")
        assertThat(info.origin).isEqualTo("upgrade")
        assertThat(info.ownedGiftId).isEqualTo("og1")
        assertThat(info.transferStarCount).isEqualTo(100)
        assertThat(info.nextTransferDate).isEqualTo(1700001000L)
    }
}
