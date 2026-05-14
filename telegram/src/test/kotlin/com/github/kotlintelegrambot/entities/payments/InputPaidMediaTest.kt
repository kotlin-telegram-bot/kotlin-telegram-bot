package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputPaidMediaTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `serializes and deserializes InputPaidMediaPhoto`() {
        val photo = InputPaidMedia.Photo(media = "attach://photo1")

        val json = gson.toJson(photo)
        val parsed = gson.fromJson(json, InputPaidMedia.Photo::class.java)

        assertThat(parsed.type).isEqualTo("photo")
        assertThat(parsed.media).isEqualTo("attach://photo1")
    }

    @Test
    fun `serializes and deserializes InputPaidMediaVideo with optional fields`() {
        val video = InputPaidMedia.Video(
            media = "attach://video1",
            thumbnail = "attach://thumb",
            cover = "attach://cover",
            startTimestamp = 5,
            width = 1280,
            height = 720,
            duration = 30,
            supportsStreaming = true,
        )

        val json = gson.toJson(video)
        val parsed = gson.fromJson(json, InputPaidMedia.Video::class.java)

        assertThat(parsed.type).isEqualTo("video")
        assertThat(parsed.media).isEqualTo("attach://video1")
        assertThat(parsed.thumbnail).isEqualTo("attach://thumb")
        assertThat(parsed.cover).isEqualTo("attach://cover")
        assertThat(parsed.startTimestamp).isEqualTo(5)
        assertThat(parsed.width).isEqualTo(1280)
        assertThat(parsed.height).isEqualTo(720)
        assertThat(parsed.duration).isEqualTo(30)
        assertThat(parsed.supportsStreaming).isTrue
    }
}
