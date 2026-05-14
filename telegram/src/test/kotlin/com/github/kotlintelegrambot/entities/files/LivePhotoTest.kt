package com.github.kotlintelegrambot.entities.files

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LivePhotoTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes LivePhoto with video and photo`() {
        val json =
            """
            {"file_id":"lp1","file_unique_id":"u1",
             "video":{"file_id":"v1","file_unique_id":"vu1","width":640,"height":480,"duration":3},
             "photo":[{"file_id":"p1","file_unique_id":"pu1","width":320,"height":240}]}
            """.trimIndent()

        val livePhoto = gson.fromJson(json, LivePhoto::class.java)

        assertThat(livePhoto.fileId).isEqualTo("lp1")
        assertThat(livePhoto.video.fileId).isEqualTo("v1")
        assertThat(livePhoto.photo).hasSize(1)
        assertThat(livePhoto.photo[0].width).isEqualTo(320)
    }
}
