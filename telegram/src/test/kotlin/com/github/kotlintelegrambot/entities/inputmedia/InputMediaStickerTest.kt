package com.github.kotlintelegrambot.entities.inputmedia

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputMediaStickerTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes InputMediaSticker with emoji`() {
        val json = """{"type":"sticker","media":"attach://sticker","emoji":"😀"}"""

        val sticker = gson.fromJson(json, InputMediaSticker::class.java)

        assertThat(sticker.type).isEqualTo("sticker")
        assertThat(sticker.media).isEqualTo("attach://sticker")
        assertThat(sticker.emoji).isEqualTo("😀")
    }

    @Test
    fun `deserializes InputMediaSticker without emoji`() {
        val json = """{"type":"sticker","media":"file_id_xyz"}"""

        val sticker = gson.fromJson(json, InputMediaSticker::class.java)

        assertThat(sticker.media).isEqualTo("file_id_xyz")
        assertThat(sticker.emoji).isNull()
    }
}
