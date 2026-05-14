package com.github.kotlintelegrambot.entities.stickers

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StickerTypeTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes regular`() {
        assertThat(gson.fromJson("\"regular\"", StickerType::class.java))
            .isEqualTo(StickerType.REGULAR)
    }

    @Test
    fun `deserializes mask`() {
        assertThat(gson.fromJson("\"mask\"", StickerType::class.java))
            .isEqualTo(StickerType.MASK)
    }

    @Test
    fun `deserializes custom_emoji`() {
        assertThat(gson.fromJson("\"custom_emoji\"", StickerType::class.java))
            .isEqualTo(StickerType.CUSTOM_EMOJI)
    }

    @Test
    fun `serializes to snake_case`() {
        assertThat(gson.toJson(StickerType.REGULAR)).isEqualTo("\"regular\"")
        assertThat(gson.toJson(StickerType.MASK)).isEqualTo("\"mask\"")
        assertThat(gson.toJson(StickerType.CUSTOM_EMOJI)).isEqualTo("\"custom_emoji\"")
    }
}
