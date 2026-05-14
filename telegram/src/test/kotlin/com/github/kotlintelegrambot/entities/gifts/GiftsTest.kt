package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GiftsTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes Gifts list`() {
        val json =
            """
            {
              "gifts":[
                {
                  "id":"a",
                  "sticker":{"file_id":"f","file_unique_id":"u","width":1,"height":1,"is_animated":false,"emoji":"🎁"},
                  "star_count":5
                }
              ]
            }
            """.trimIndent()

        val gifts = gson.fromJson(json, Gifts::class.java)

        assertThat(gifts.gifts).hasSize(1)
        assertThat(gifts.gifts[0].id).isEqualTo("a")
    }
}
