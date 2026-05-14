package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PaidMediaTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes PaidMediaPreview by 'preview' discriminator`() {
        val json = """{"type":"preview","width":640,"height":480,"duration":12}"""

        val media = gson.fromJson(json, PaidMedia::class.java)

        assertThat(media).isInstanceOf(PaidMedia.Preview::class.java)
        media as PaidMedia.Preview
        assertThat(media.width).isEqualTo(640)
        assertThat(media.height).isEqualTo(480)
        assertThat(media.duration).isEqualTo(12)
    }

    @Test
    fun `deserializes PaidMediaPhoto with photo size list`() {
        val json = """
            {"type":"photo",
             "photo":[
               {"file_id":"AAA","file_unique_id":"u1","width":90,"height":90,"file_size":1000},
               {"file_id":"BBB","file_unique_id":"u2","width":320,"height":320}
             ]}
        """.trimIndent()

        val media = gson.fromJson(json, PaidMedia::class.java)

        assertThat(media).isInstanceOf(PaidMedia.Photo::class.java)
        media as PaidMedia.Photo
        assertThat(media.photo).hasSize(2)
        assertThat(media.photo[0].fileId).isEqualTo("AAA")
        assertThat(media.photo[1].width).isEqualTo(320)
    }

    @Test
    fun `deserializes PaidMediaVideo`() {
        val json = """
            {"type":"video",
             "video":{"file_id":"V","file_unique_id":"u","width":1280,"height":720,"duration":30}}
        """.trimIndent()

        val media = gson.fromJson(json, PaidMedia::class.java)

        assertThat(media).isInstanceOf(PaidMedia.Video::class.java)
        media as PaidMedia.Video
        assertThat(media.video.fileId).isEqualTo("V")
        assertThat(media.video.duration).isEqualTo(30)
    }

    @Test
    fun `deserializes PaidMediaLivePhoto`() {
        val json = """{"type":"live_photo"}"""

        val media = gson.fromJson(json, PaidMedia::class.java)

        assertThat(media).isInstanceOf(PaidMedia.LivePhoto::class.java)
    }
}
