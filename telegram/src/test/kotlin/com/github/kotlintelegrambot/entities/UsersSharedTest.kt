package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("DEPRECATION")
class UsersSharedTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes legacy UserShared`() {
        val json = """{"request_id":1,"user_id":4242}"""

        val parsed = gson.fromJson(json, UserShared::class.java)

        assertThat(parsed.requestId).isEqualTo(1)
        assertThat(parsed.userId).isEqualTo(4242L)
    }

    @Test
    fun `deserializes UsersShared with legacy user_ids array (7_0 shape)`() {
        val json = """{"request_id":3,"user_ids":[1,2,3]}"""

        val parsed = gson.fromJson(json, UsersShared::class.java)

        assertThat(parsed.requestId).isEqualTo(3)
        assertThat(parsed.userIds).containsExactly(1L, 2L, 3L)
        assertThat(parsed.users).isNull()
    }

    @Test
    fun `deserializes UsersShared with users array of SharedUser (7_2 shape)`() {
        val json =
            """
            {
              "request_id": 11,
              "users": [
                {
                  "user_id": 100,
                  "first_name": "Alice",
                  "last_name": "Doe",
                  "username": "alice",
                  "photo": [
                    {"file_id":"f1","file_unique_id":"u1","width":320,"height":240}
                  ]
                },
                { "user_id": 200 }
              ]
            }
            """.trimIndent()

        val parsed = gson.fromJson(json, UsersShared::class.java)

        assertThat(parsed.requestId).isEqualTo(11)
        assertThat(parsed.users).hasSize(2)
        val first = parsed.users!![0]
        assertThat(first.userId).isEqualTo(100L)
        assertThat(first.firstName).isEqualTo("Alice")
        assertThat(first.lastName).isEqualTo("Doe")
        assertThat(first.username).isEqualTo("alice")
        assertThat(first.photo).hasSize(1)
        assertThat(first.photo!![0].fileId).isEqualTo("f1")
        assertThat(first.photo!![0].width).isEqualTo(320)

        val second = parsed.users!![1]
        assertThat(second.userId).isEqualTo(200L)
        assertThat(second.firstName).isNull()
        assertThat(second.photo).isNull()
    }
}
