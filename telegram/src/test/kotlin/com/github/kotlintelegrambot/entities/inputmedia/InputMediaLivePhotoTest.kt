package com.github.kotlintelegrambot.entities.inputmedia

import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputMediaLivePhotoTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes InputMediaLivePhoto with all fields`() {
        val json =
            """
            {"type":"live_photo","video":"attach://v","photo":"attach://p",
             "caption":"Hi","parse_mode":"HTML",
             "caption_entities":[{"type":"bold","offset":0,"length":2}],
             "show_caption_above_media":true}
            """.trimIndent()

        val media = gson.fromJson(json, InputMediaLivePhoto::class.java)

        assertThat(media.type).isEqualTo("live_photo")
        assertThat(media.video).isEqualTo("attach://v")
        assertThat(media.photo).isEqualTo("attach://p")
        assertThat(media.caption).isEqualTo("Hi")
        assertThat(media.parseMode).isEqualTo(ParseMode.HTML)
        assertThat(media.captionEntities).hasSize(1)
        assertThat(media.showCaptionAboveMedia).isTrue
    }

    @Test
    fun `deserializes minimal InputMediaLivePhoto`() {
        val json = """{"type":"live_photo","video":"v","photo":"p"}"""

        val media = gson.fromJson(json, InputMediaLivePhoto::class.java)

        assertThat(media.caption).isNull()
        assertThat(media.parseMode).isNull()
        assertThat(media.captionEntities).isNull()
        assertThat(media.showCaptionAboveMedia).isNull()
    }
}
