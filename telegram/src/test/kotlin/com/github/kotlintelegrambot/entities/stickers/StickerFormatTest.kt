package com.github.kotlintelegrambot.entities.stickers

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StickerFormatTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes static`() {
        assertThat(gson.fromJson("\"static\"", StickerFormat::class.java))
            .isEqualTo(StickerFormat.STATIC)
    }

    @Test
    fun `deserializes animated`() {
        assertThat(gson.fromJson("\"animated\"", StickerFormat::class.java))
            .isEqualTo(StickerFormat.ANIMATED)
    }

    @Test
    fun `deserializes video`() {
        assertThat(gson.fromJson("\"video\"", StickerFormat::class.java))
            .isEqualTo(StickerFormat.VIDEO)
    }

    @Test
    fun `serializes to lower-case discriminators`() {
        assertThat(gson.toJson(StickerFormat.STATIC)).isEqualTo("\"static\"")
        assertThat(gson.toJson(StickerFormat.ANIMATED)).isEqualTo("\"animated\"")
        assertThat(gson.toJson(StickerFormat.VIDEO)).isEqualTo("\"video\"")
    }
}
