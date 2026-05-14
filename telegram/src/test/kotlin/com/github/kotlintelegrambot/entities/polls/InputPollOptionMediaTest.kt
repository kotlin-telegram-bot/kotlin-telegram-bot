package com.github.kotlintelegrambot.entities.polls

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputPollOptionMediaTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes Photo InputPollOptionMedia`() {
        val json = """{"type":"photo","media":"attach://p"}"""

        val media = gson.fromJson(json, InputPollOptionMedia::class.java)

        assertThat(media).isInstanceOf(InputPollOptionMedia.Photo::class.java)
        assertThat(media.media).isEqualTo("attach://p")
    }

    @Test
    fun `deserializes Video InputPollOptionMedia`() {
        val json = """{"type":"video","media":"vid_id"}"""

        val media = gson.fromJson(json, InputPollOptionMedia::class.java)

        assertThat(media).isInstanceOf(InputPollOptionMedia.Video::class.java)
    }

    @Test
    fun `deserializes LivePhoto InputPollOptionMedia`() {
        val json = """{"type":"live_photo","media":"lp_id"}"""

        val media = gson.fromJson(json, InputPollOptionMedia::class.java)

        assertThat(media).isInstanceOf(InputPollOptionMedia.LivePhoto::class.java)
    }

    @Test
    fun `deserializes Animation InputPollOptionMedia`() {
        val json = """{"type":"animation","media":"a_id"}"""

        val media = gson.fromJson(json, InputPollOptionMedia::class.java)

        assertThat(media).isInstanceOf(InputPollOptionMedia.Animation::class.java)
    }
}
