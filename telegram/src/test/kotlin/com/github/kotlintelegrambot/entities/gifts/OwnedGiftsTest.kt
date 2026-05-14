package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OwnedGiftsTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes OwnedGifts list`() {
        val stickerJson =
            """{"file_id":"f","file_unique_id":"u","width":1,"height":1,"is_animated":false,"emoji":"🎁"}"""
        val json = """
            {
              "total_count":2,
              "gifts":[
                {"type":"regular","gift":{"id":"g","sticker":$stickerJson,"star_count":10},"send_date":1700000000}
              ],
              "next_offset":"next"
            }
        """.trimIndent()

        val ownedGifts = gson.fromJson(json, OwnedGifts::class.java)

        assertThat(ownedGifts.totalCount).isEqualTo(2)
        assertThat(ownedGifts.gifts).hasSize(1)
        assertThat(ownedGifts.gifts[0]).isInstanceOf(OwnedGift.Regular::class.java)
        assertThat(ownedGifts.nextOffset).isEqualTo("next")
    }
}
