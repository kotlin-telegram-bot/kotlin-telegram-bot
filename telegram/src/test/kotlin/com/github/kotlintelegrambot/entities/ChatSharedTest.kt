package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatSharedTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes ChatShared with only required fields (6_5 shape)`() {
        val json = """{"request_id":4,"chat_id":-100123456}"""

        val parsed = gson.fromJson(json, ChatShared::class.java)

        assertThat(parsed.requestId).isEqualTo(4)
        assertThat(parsed.chatId).isEqualTo(-100123456L)
        assertThat(parsed.title).isNull()
        assertThat(parsed.username).isNull()
        assertThat(parsed.photo).isNull()
    }

    @Test
    fun `deserializes ChatShared with title username and photo (7_2 shape)`() {
        val json =
            """
            {
              "request_id": 5,
              "chat_id": -100987,
              "title": "My Channel",
              "username": "mychannel",
              "photo": [
                {"file_id":"a","file_unique_id":"ua","width":160,"height":160,"file_size":4096},
                {"file_id":"b","file_unique_id":"ub","width":640,"height":640}
              ]
            }
            """.trimIndent()

        val parsed = gson.fromJson(json, ChatShared::class.java)

        assertThat(parsed.requestId).isEqualTo(5)
        assertThat(parsed.chatId).isEqualTo(-100987L)
        assertThat(parsed.title).isEqualTo("My Channel")
        assertThat(parsed.username).isEqualTo("mychannel")
        assertThat(parsed.photo).hasSize(2)
        assertThat(parsed.photo!![0].fileId).isEqualTo("a")
        assertThat(parsed.photo!![0].fileSize).isEqualTo(4096)
        assertThat(parsed.photo!![1].width).isEqualTo(640)
    }
}
