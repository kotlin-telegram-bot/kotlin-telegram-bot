package com.github.kotlintelegrambot.entities.suggestedposts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DirectMessagesTopicTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes DirectMessagesTopic with user`() {
        val json = """{"topic_id":11,"user":{"id":42,"is_bot":false,"first_name":"Alice"}}"""

        val topic = gson.fromJson(json, DirectMessagesTopic::class.java)

        assertThat(topic.topicId).isEqualTo(11)
        assertThat(topic.user?.firstName).isEqualTo("Alice")
    }

    @Test
    fun `deserializes DirectMessagesTopic without user`() {
        val json = """{"topic_id":1}"""

        val topic = gson.fromJson(json, DirectMessagesTopic::class.java)

        assertThat(topic.topicId).isEqualTo(1)
        assertThat(topic.user).isNull()
    }
}
