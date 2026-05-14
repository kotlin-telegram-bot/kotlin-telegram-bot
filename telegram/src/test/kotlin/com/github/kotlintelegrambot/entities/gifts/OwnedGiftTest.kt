package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OwnedGiftTest {

    private val gson = GsonFactory.createForApiClient()

    private val stickerJson =
        """{"file_id":"f","file_unique_id":"u","width":1,"height":1,"is_animated":false,"emoji":"🎁"}"""

    @Test
    fun `deserializes Regular OwnedGift via sealed adapter`() {
        val json = """
            {
              "type":"regular",
              "gift":{"id":"g","sticker":$stickerJson,"star_count":10},
              "owned_gift_id":"og1",
              "send_date":1700000000,
              "text":"Hello",
              "is_private":true,
              "is_saved":false,
              "can_be_upgraded":true,
              "was_refunded":false,
              "convert_star_count":7,
              "prepaid_upgrade_star_count":3
            }
        """.trimIndent()

        val owned = gson.fromJson(json, OwnedGift::class.java)

        assertThat(owned).isInstanceOf(OwnedGift.Regular::class.java)
        val regular = owned as OwnedGift.Regular
        assertThat(regular.type).isEqualTo("regular")
        assertThat(regular.gift.id).isEqualTo("g")
        assertThat(regular.ownedGiftId).isEqualTo("og1")
        assertThat(regular.sendDate).isEqualTo(1700000000L)
        assertThat(regular.text).isEqualTo("Hello")
        assertThat(regular.isPrivate).isTrue()
        assertThat(regular.canBeUpgraded).isTrue()
        assertThat(regular.convertStarCount).isEqualTo(7)
        assertThat(regular.prepaidUpgradeStarCount).isEqualTo(3)
    }

    @Test
    fun `deserializes Unique OwnedGift via sealed adapter`() {
        val json = """
            {
              "type":"unique",
              "gift":{
                "base_name":"Dragon","name":"Dragon-7","number":7,
                "model":{"name":"M","sticker":$stickerJson,"rarity_per_mille":1},
                "symbol":{"name":"S","sticker":$stickerJson,"rarity_per_mille":2},
                "backdrop":{"name":"B","colors":{"center_color":1,"edge_color":2,"symbol_color":3,"text_color":4},"rarity_per_mille":3}
              },
              "owned_gift_id":"og2",
              "send_date":1700000001,
              "is_saved":true,
              "is_owned":true,
              "can_be_transferred":true,
              "transfer_star_count":25,
              "next_transfer_date":1700001000
            }
        """.trimIndent()

        val owned = gson.fromJson(json, OwnedGift::class.java)

        assertThat(owned).isInstanceOf(OwnedGift.Unique::class.java)
        val unique = owned as OwnedGift.Unique
        assertThat(unique.type).isEqualTo("unique")
        assertThat(unique.gift.baseName).isEqualTo("Dragon")
        assertThat(unique.ownedGiftId).isEqualTo("og2")
        assertThat(unique.sendDate).isEqualTo(1700000001L)
        assertThat(unique.isSaved).isTrue()
        assertThat(unique.canBeTransferred).isTrue()
        assertThat(unique.transferStarCount).isEqualTo(25)
        assertThat(unique.nextTransferDate).isEqualTo(1700001000L)
    }
}
