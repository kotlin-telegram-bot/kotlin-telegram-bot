package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GiftTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes Gift with full fields`() {
        val json =
            """
            {
              "id":"gift_1",
              "sticker":{
                "file_id":"f1",
                "file_unique_id":"u1",
                "width":100,
                "height":100,
                "is_animated":false,
                "emoji":"🎁"
              },
              "star_count":50,
              "upgrade_star_count":200,
              "total_count":10000,
              "remaining_count":50
            }
            """.trimIndent()

        val gift = gson.fromJson(json, Gift::class.java)

        assertThat(gift.id).isEqualTo("gift_1")
        assertThat(gift.sticker.fileId).isEqualTo("f1")
        assertThat(gift.starCount).isEqualTo(50)
        assertThat(gift.upgradeStarCount).isEqualTo(200)
        assertThat(gift.totalCount).isEqualTo(10000)
        assertThat(gift.remainingCount).isEqualTo(50)
    }

    @Test
    fun `deserializes Gift with only required fields`() {
        val json =
            """
            {
              "id":"g2",
              "sticker":{"file_id":"f2","file_unique_id":"u2","width":1,"height":1,"is_animated":false,"emoji":null},
              "star_count":10
            }
            """.trimIndent()

        val gift = gson.fromJson(json, Gift::class.java)

        assertThat(gift.id).isEqualTo("g2")
        assertThat(gift.starCount).isEqualTo(10)
        assertThat(gift.upgradeStarCount).isNull()
        assertThat(gift.totalCount).isNull()
        assertThat(gift.remainingCount).isNull()
    }
}
