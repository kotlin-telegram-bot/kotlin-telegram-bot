package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UniqueGiftTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes UniqueGift with nested model symbol and backdrop`() {
        val sticker = """{"file_id":"f","file_unique_id":"u","width":1,"height":1,"is_animated":false,"emoji":"🎁"}"""
        val json = """
            {
              "base_name":"Dragon",
              "name":"Dragon-7",
              "number":7,
              "model":{"name":"Cool Dragon","sticker":$sticker,"rarity_per_mille":10},
              "symbol":{"name":"Flame","sticker":$sticker,"rarity_per_mille":50},
              "backdrop":{
                "name":"Sky",
                "colors":{"center_color":1,"edge_color":2,"symbol_color":3,"text_color":4},
                "rarity_per_mille":25
              }
            }
        """.trimIndent()

        val uniqueGift = gson.fromJson(json, UniqueGift::class.java)

        assertThat(uniqueGift.baseName).isEqualTo("Dragon")
        assertThat(uniqueGift.name).isEqualTo("Dragon-7")
        assertThat(uniqueGift.number).isEqualTo(7)
        assertThat(uniqueGift.model.name).isEqualTo("Cool Dragon")
        assertThat(uniqueGift.model.rarityPerMille).isEqualTo(10)
        assertThat(uniqueGift.symbol.name).isEqualTo("Flame")
        assertThat(uniqueGift.backdrop.name).isEqualTo("Sky")
        assertThat(uniqueGift.backdrop.colors.centerColor).isEqualTo(1L)
        assertThat(uniqueGift.backdrop.colors.edgeColor).isEqualTo(2L)
        assertThat(uniqueGift.backdrop.colors.symbolColor).isEqualTo(3L)
        assertThat(uniqueGift.backdrop.colors.textColor).isEqualTo(4L)
        assertThat(uniqueGift.backdrop.rarityPerMille).isEqualTo(25)
    }
}
