package com.github.kotlintelegrambot.entities.stories

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputStoryContentTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes Photo InputStoryContent via sealed adapter`() {
        val json = """{"type":"photo","photo":"attach://photo.jpg"}"""

        val result = gson.fromJson(json, InputStoryContent::class.java)

        assertThat(result).isInstanceOf(InputStoryContent.Photo::class.java)
        assertThat((result as InputStoryContent.Photo).photo).isEqualTo("attach://photo.jpg")
    }

    @Test
    fun `deserializes Video InputStoryContent via sealed adapter`() {
        val json = """
            {"type":"video","video":"attach://v.mp4","duration":42.5,"cover_frame_timestamp":2.0,"is_animation":true}
        """.trimIndent()

        val result = gson.fromJson(json, InputStoryContent::class.java)

        assertThat(result).isInstanceOf(InputStoryContent.Video::class.java)
        val video = result as InputStoryContent.Video
        assertThat(video.video).isEqualTo("attach://v.mp4")
        assertThat(video.duration).isEqualTo(42.5f)
        assertThat(video.coverFrameTimestamp).isEqualTo(2.0f)
        assertThat(video.isAnimation).isTrue()
    }
}
