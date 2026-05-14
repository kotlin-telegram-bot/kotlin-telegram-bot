package com.github.kotlintelegrambot.entities.polls

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputPollMediaTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes Photo InputPollMedia by discriminator`() {
        val json = """{"type":"photo","media":"attach://photo1"}"""

        val media = gson.fromJson(json, InputPollMedia::class.java)

        assertThat(media).isInstanceOf(InputPollMedia.Photo::class.java)
        assertThat(media.media).isEqualTo("attach://photo1")
    }

    @Test
    fun `deserializes Video InputPollMedia by discriminator`() {
        val json = """{"type":"video","media":"https://example.com/v.mp4"}"""

        val media = gson.fromJson(json, InputPollMedia::class.java)

        assertThat(media).isInstanceOf(InputPollMedia.Video::class.java)
        assertThat(media.media).isEqualTo("https://example.com/v.mp4")
    }

    @Test
    fun `deserializes LivePhoto InputPollMedia by discriminator`() {
        val json = """{"type":"live_photo","media":"file_id_xyz"}"""

        val media = gson.fromJson(json, InputPollMedia::class.java)

        assertThat(media).isInstanceOf(InputPollMedia.LivePhoto::class.java)
    }

    @Test
    fun `deserializes Animation InputPollMedia by discriminator`() {
        val json = """{"type":"animation","media":"attach://anim"}"""

        val media = gson.fromJson(json, InputPollMedia::class.java)

        assertThat(media).isInstanceOf(InputPollMedia.Animation::class.java)
    }
}
