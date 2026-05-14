package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserProfileAudiosTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes UserProfileAudios with audios`() {
        val json = """
            {"total_count":2,
             "audios":[
               {"file_id":"a1","file_unique_id":"u1","duration":30},
               {"file_id":"a2","file_unique_id":"u2","duration":60,"performer":"Alice"}
             ]}
        """.trimIndent()

        val audios = gson.fromJson(json, UserProfileAudios::class.java)

        assertThat(audios.totalCount).isEqualTo(2)
        assertThat(audios.audios).hasSize(2)
        assertThat(audios.audios[0].fileId).isEqualTo("a1")
        assertThat(audios.audios[1].performer).isEqualTo("Alice")
    }

    @Test
    fun `deserializes empty UserProfileAudios`() {
        val json = """{"total_count":0,"audios":[]}"""

        val audios = gson.fromJson(json, UserProfileAudios::class.java)

        assertThat(audios.totalCount).isEqualTo(0)
        assertThat(audios.audios).isEmpty()
    }
}
