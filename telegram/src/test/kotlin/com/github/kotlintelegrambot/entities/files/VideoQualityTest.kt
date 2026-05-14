package com.github.kotlintelegrambot.entities.files

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VideoQualityTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes VideoQuality with all fields`() {
        val json = """
            {"file_id":"vq1","file_unique_id":"u1","width":1920,"height":1080,
             "bitrate":4500000,"mime_type":"video/mp4","file_size":52428800}
        """.trimIndent()

        val quality = gson.fromJson(json, VideoQuality::class.java)

        assertThat(quality.fileId).isEqualTo("vq1")
        assertThat(quality.fileUniqueId).isEqualTo("u1")
        assertThat(quality.width).isEqualTo(1920)
        assertThat(quality.height).isEqualTo(1080)
        assertThat(quality.bitrate).isEqualTo(4500000)
        assertThat(quality.mimeType).isEqualTo("video/mp4")
        assertThat(quality.fileSize).isEqualTo(52428800L)
    }

    @Test
    fun `deserializes minimal VideoQuality`() {
        val json = """
            {"file_id":"vq2","file_unique_id":"u2","width":640,"height":360,"bitrate":900000}
        """.trimIndent()

        val quality = gson.fromJson(json, VideoQuality::class.java)

        assertThat(quality.mimeType).isNull()
        assertThat(quality.fileSize).isNull()
    }
}
