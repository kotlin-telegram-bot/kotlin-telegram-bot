package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ForumTopicTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes ForumTopic with all fields`() {
        val json =
            """
            {"message_thread_id":12345,"name":"General Discussion","icon_color":7322096,"icon_custom_emoji_id":"5312536423851630001"}
            """.trimIndent()

        val topic = gson.fromJson(json, ForumTopic::class.java)

        assertThat(topic.messageThreadId).isEqualTo(12345L)
        assertThat(topic.name).isEqualTo("General Discussion")
        assertThat(topic.iconColor).isEqualTo(7322096)
        assertThat(topic.iconCustomEmojiId).isEqualTo("5312536423851630001")
    }

    @Test
    fun `deserializes ForumTopic without optional icon_custom_emoji_id`() {
        val json = """{"message_thread_id":42,"name":"Topic","icon_color":16766590}"""

        val topic = gson.fromJson(json, ForumTopic::class.java)

        assertThat(topic.messageThreadId).isEqualTo(42L)
        assertThat(topic.name).isEqualTo("Topic")
        assertThat(topic.iconColor).isEqualTo(16766590)
        assertThat(topic.iconCustomEmojiId).isNull()
    }

    @Test
    fun `deserializes ForumTopicCreated`() {
        val json =
            """
            {"name":"My Topic","icon_color":9367192,"icon_custom_emoji_id":"emoji-1"}
            """.trimIndent()

        val created = gson.fromJson(json, ForumTopicCreated::class.java)

        assertThat(created.name).isEqualTo("My Topic")
        assertThat(created.iconColor).isEqualTo(9367192)
        assertThat(created.iconCustomEmojiId).isEqualTo("emoji-1")
    }

    @Test
    fun `deserializes ForumTopicCreated without icon_custom_emoji_id`() {
        val json = """{"name":"My Topic","icon_color":9367192}"""

        val created = gson.fromJson(json, ForumTopicCreated::class.java)

        assertThat(created.name).isEqualTo("My Topic")
        assertThat(created.iconColor).isEqualTo(9367192)
        assertThat(created.iconCustomEmojiId).isNull()
    }

    @Test
    fun `deserializes empty ForumTopicClosed`() {
        val closed = gson.fromJson("{}", ForumTopicClosed::class.java)

        assertThat(closed).isNotNull
    }

    @Test
    fun `deserializes empty ForumTopicReopened`() {
        val reopened = gson.fromJson("{}", ForumTopicReopened::class.java)

        assertThat(reopened).isNotNull
    }

    @Test
    fun `deserializes ForumTopicEdited with both fields`() {
        val json = """{"name":"Renamed","icon_custom_emoji_id":"emoji-99"}"""

        val edited = gson.fromJson(json, ForumTopicEdited::class.java)

        assertThat(edited.name).isEqualTo("Renamed")
        assertThat(edited.iconCustomEmojiId).isEqualTo("emoji-99")
    }

    @Test
    fun `deserializes ForumTopicEdited with no fields`() {
        val edited = gson.fromJson("{}", ForumTopicEdited::class.java)

        assertThat(edited.name).isNull()
        assertThat(edited.iconCustomEmojiId).isNull()
    }

    @Test
    fun `deserializes ForumTopicEdited with only name`() {
        val edited = gson.fromJson("""{"name":"Just renamed"}""", ForumTopicEdited::class.java)

        assertThat(edited.name).isEqualTo("Just renamed")
        assertThat(edited.iconCustomEmojiId).isNull()
    }

    @Test
    fun `deserializes empty GeneralForumTopicHidden`() {
        val hidden = gson.fromJson("{}", GeneralForumTopicHidden::class.java)

        assertThat(hidden).isNotNull
    }

    @Test
    fun `deserializes empty GeneralForumTopicUnhidden`() {
        val unhidden = gson.fromJson("{}", GeneralForumTopicUnhidden::class.java)

        assertThat(unhidden).isNotNull
    }

    @Test
    fun `deserializes WriteAccessAllowed with all fields`() {
        val json =
            """
            {"from_request":true,"web_app_name":"My Web App","from_attachment_menu":false}
            """.trimIndent()

        val allowed = gson.fromJson(json, WriteAccessAllowed::class.java)

        assertThat(allowed.fromRequest).isTrue
        assertThat(allowed.webAppName).isEqualTo("My Web App")
        assertThat(allowed.fromAttachmentMenu).isFalse
    }

    @Test
    fun `deserializes empty WriteAccessAllowed`() {
        val allowed = gson.fromJson("{}", WriteAccessAllowed::class.java)

        assertThat(allowed.fromRequest).isNull()
        assertThat(allowed.webAppName).isNull()
        assertThat(allowed.fromAttachmentMenu).isNull()
    }

    @Test
    fun `deserializes WriteAccessAllowed from attachment menu only`() {
        val json = """{"from_attachment_menu":true}"""

        val allowed = gson.fromJson(json, WriteAccessAllowed::class.java)

        assertThat(allowed.fromRequest).isNull()
        assertThat(allowed.webAppName).isNull()
        assertThat(allowed.fromAttachmentMenu).isTrue
    }
}
