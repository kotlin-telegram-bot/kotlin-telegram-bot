package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("DEPRECATION")
class KeyboardButtonRequestTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes KeyboardButtonRequestUser`() {
        val json = """{"request_id":1,"user_is_bot":false,"user_is_premium":true}"""

        val parsed = gson.fromJson(json, KeyboardButtonRequestUser::class.java)

        assertThat(parsed.requestId).isEqualTo(1)
        assertThat(parsed.userIsBot).isFalse
        assertThat(parsed.userIsPremium).isTrue
    }

    @Test
    fun `deserializes KeyboardButtonRequestUsers with all optional fields`() {
        val json =
            """
            {
              "request_id": 7,
              "user_is_bot": false,
              "user_is_premium": true,
              "max_quantity": 5,
              "request_name": true,
              "request_username": true,
              "request_photo": false
            }
            """.trimIndent()

        val parsed = gson.fromJson(json, KeyboardButtonRequestUsers::class.java)

        assertThat(parsed.requestId).isEqualTo(7)
        assertThat(parsed.userIsBot).isFalse
        assertThat(parsed.userIsPremium).isTrue
        assertThat(parsed.maxQuantity).isEqualTo(5)
        assertThat(parsed.requestName).isTrue
        assertThat(parsed.requestUsername).isTrue
        assertThat(parsed.requestPhoto).isFalse
    }

    @Test
    fun `deserializes KeyboardButtonRequestUsers with only required fields`() {
        val json = """{"request_id":2}"""

        val parsed = gson.fromJson(json, KeyboardButtonRequestUsers::class.java)

        assertThat(parsed.requestId).isEqualTo(2)
        assertThat(parsed.userIsBot).isNull()
        assertThat(parsed.maxQuantity).isNull()
        assertThat(parsed.requestPhoto).isNull()
    }

    @Test
    fun `deserializes KeyboardButtonRequestChat`() {
        val json =
            """
            {
              "request_id": 9,
              "chat_is_channel": true,
              "chat_is_forum": false,
              "chat_has_username": true,
              "chat_is_created": false,
              "bot_is_member": true,
              "request_title": true,
              "request_username": false,
              "request_photo": true
            }
            """.trimIndent()

        val parsed = gson.fromJson(json, KeyboardButtonRequestChat::class.java)

        assertThat(parsed.requestId).isEqualTo(9)
        assertThat(parsed.chatIsChannel).isTrue
        assertThat(parsed.chatIsForum).isFalse
        assertThat(parsed.chatHasUsername).isTrue
        assertThat(parsed.chatIsCreated).isFalse
        assertThat(parsed.botIsMember).isTrue
        assertThat(parsed.requestTitle).isTrue
        assertThat(parsed.requestUsername).isFalse
        assertThat(parsed.requestPhoto).isTrue
        assertThat(parsed.userAdministratorRights).isNull()
        assertThat(parsed.botAdministratorRights).isNull()
    }
}
