package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputPaidMediaLivePhotoTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes InputPaidMediaLivePhoto`() {
        val json = """{"type":"live_photo","video":"attach://v","photo":"attach://p"}"""

        val media = gson.fromJson(json, InputPaidMediaLivePhoto::class.java)

        assertThat(media.type).isEqualTo("live_photo")
        assertThat(media.video).isEqualTo("attach://v")
        assertThat(media.photo).isEqualTo("attach://p")
    }
}
