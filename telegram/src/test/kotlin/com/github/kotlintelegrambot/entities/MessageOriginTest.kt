package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MessageOriginTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes MessageOriginUser by 'user' discriminator`() {
        val json =
            """
            {"type":"user","date":1700000000,"sender_user":{"id":42,"is_bot":false,"first_name":"Alice"}}
            """.trimIndent()

        val origin = gson.fromJson(json, MessageOrigin::class.java)

        assertThat(origin).isInstanceOf(MessageOrigin.User::class.java)
        origin as MessageOrigin.User
        assertThat(origin.date).isEqualTo(1700000000L)
        assertThat(origin.senderUser.id).isEqualTo(42L)
        assertThat(origin.senderUser.firstName).isEqualTo("Alice")
    }

    @Test
    fun `deserializes MessageOriginHiddenUser by 'hidden_user' discriminator`() {
        val json = """{"type":"hidden_user","date":1700000000,"sender_user_name":"Bob"}"""

        val origin = gson.fromJson(json, MessageOrigin::class.java)

        assertThat(origin).isInstanceOf(MessageOrigin.HiddenUser::class.java)
        origin as MessageOrigin.HiddenUser
        assertThat(origin.senderUserName).isEqualTo("Bob")
    }

    @Test
    fun `deserializes MessageOriginChat by 'chat' discriminator`() {
        val json =
            """
            {"type":"chat","date":1700000000,"sender_chat":{"id":-100,"type":"supergroup"},"author_signature":"Admin"}
            """.trimIndent()

        val origin = gson.fromJson(json, MessageOrigin::class.java)

        assertThat(origin).isInstanceOf(MessageOrigin.Chat::class.java)
        origin as MessageOrigin.Chat
        assertThat(origin.senderChat.id).isEqualTo(-100L)
        assertThat(origin.authorSignature).isEqualTo("Admin")
    }

    @Test
    fun `deserializes MessageOriginChannel by 'channel' discriminator`() {
        val json =
            """
            {"type":"channel","date":1700000000,"chat":{"id":-200,"type":"channel"},"message_id":7}
            """.trimIndent()

        val origin = gson.fromJson(json, MessageOrigin::class.java)

        assertThat(origin).isInstanceOf(MessageOrigin.Channel::class.java)
        origin as MessageOrigin.Channel
        assertThat(origin.chat.id).isEqualTo(-200L)
        assertThat(origin.messageId).isEqualTo(7L)
    }
}
