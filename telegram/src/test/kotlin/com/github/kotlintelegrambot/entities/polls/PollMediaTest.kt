package com.github.kotlintelegrambot.entities.polls

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PollMediaTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes Photo PollMedia by 'photo' discriminator`() {
        val json = """
            {"type":"photo","photo":[{"file_id":"p1","file_unique_id":"u1","width":640,"height":480}]}
        """.trimIndent()

        val media = gson.fromJson(json, PollMedia::class.java)

        assertThat(media).isInstanceOf(PollMedia.Photo::class.java)
        media as PollMedia.Photo
        assertThat(media.photo).hasSize(1)
        assertThat(media.photo[0].fileId).isEqualTo("p1")
    }

    @Test
    fun `deserializes Video PollMedia by 'video' discriminator`() {
        val json = """
            {"type":"video","video":{"file_id":"v1","file_unique_id":"u1","width":1280,"height":720,"duration":10}}
        """.trimIndent()

        val media = gson.fromJson(json, PollMedia::class.java)

        assertThat(media).isInstanceOf(PollMedia.Video::class.java)
        media as PollMedia.Video
        assertThat(media.video.fileId).isEqualTo("v1")
    }

    @Test
    fun `deserializes LivePhoto PollMedia by 'live_photo' discriminator`() {
        val json = """
            {"type":"live_photo","live_photo":{"file_id":"lp1","file_unique_id":"u1",
             "video":{"file_id":"v1","file_unique_id":"vu1","width":640,"height":480,"duration":3},
             "photo":[{"file_id":"p1","file_unique_id":"pu1","width":320,"height":240}]}}
        """.trimIndent()

        val media = gson.fromJson(json, PollMedia::class.java)

        assertThat(media).isInstanceOf(PollMedia.LivePhoto::class.java)
        media as PollMedia.LivePhoto
        assertThat(media.livePhoto.fileId).isEqualTo("lp1")
    }

    @Test
    fun `deserializes Animation PollMedia by 'animation' discriminator`() {
        val json = """
            {"type":"animation","animation":{"file_id":"a1","file_unique_id":"u1","width":480,"height":320,"duration":5}}
        """.trimIndent()

        val media = gson.fromJson(json, PollMedia::class.java)

        assertThat(media).isInstanceOf(PollMedia.Animation::class.java)
        media as PollMedia.Animation
        assertThat(media.animation.fileId).isEqualTo("a1")
    }
}
