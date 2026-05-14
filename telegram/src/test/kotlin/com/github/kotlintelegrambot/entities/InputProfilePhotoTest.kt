package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputProfilePhotoTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes Static InputProfilePhoto via sealed adapter`() {
        val json = """{"type":"static","photo":"attach://avatar.jpg"}"""

        val result = gson.fromJson(json, InputProfilePhoto::class.java)

        assertThat(result).isInstanceOf(InputProfilePhoto.Static::class.java)
        val static = result as InputProfilePhoto.Static
        assertThat(static.photo).isEqualTo("attach://avatar.jpg")
    }

    @Test
    fun `deserializes Animated InputProfilePhoto via sealed adapter`() {
        val json = """{"type":"animated","animation":"attach://avatar.mp4","main_frame_timestamp":1.5}"""

        val result = gson.fromJson(json, InputProfilePhoto::class.java)

        assertThat(result).isInstanceOf(InputProfilePhoto.Animated::class.java)
        val animated = result as InputProfilePhoto.Animated
        assertThat(animated.animation).isEqualTo("attach://avatar.mp4")
        assertThat(animated.mainFrameTimestamp).isEqualTo(1.5f)
    }
}
