package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ReplyParametersTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `serializes only message_id when nothing else is set`() {
        val replyParameters = ReplyParameters(messageId = 42L)

        val json = gson.toJson(replyParameters)

        assertThat(json).isEqualTo("""{"message_id":42}""")
    }

    @Test
    fun `serializes chat_id from cross-chat replies`() {
        val replyParameters = ReplyParameters(
            messageId = 42L,
            chatId = ChatId.fromId(123456L),
        )

        val json = gson.toJson(replyParameters)

        assertThat(json).contains("\"message_id\":42")
        assertThat(json).contains("\"chat_id\":123456")
    }

    @Test
    fun `serializes quote-related fields with correct snake_case keys`() {
        val replyParameters = ReplyParameters(
            messageId = 42L,
            quote = "the quoted part",
            quoteParseMode = ParseMode.MARKDOWN_V2,
            quotePosition = 7,
        )

        val json = gson.toJson(replyParameters)

        assertThat(json).contains("\"quote\":\"the quoted part\"")
        assertThat(json).contains("\"quote_parse_mode\":\"MarkdownV2\"")
        assertThat(json).contains("\"quote_position\":7")
    }

    @Test
    fun `serializes allow_sending_without_reply`() {
        val replyParameters = ReplyParameters(
            messageId = 42L,
            allowSendingWithoutReply = true,
        )

        val json = gson.toJson(replyParameters)

        assertThat(json).contains("\"allow_sending_without_reply\":true")
    }

    @Test
    fun `serializes future-version fields (checklist_task_id, poll_option_id)`() {
        val replyParameters = ReplyParameters(
            messageId = 42L,
            checklistTaskId = 3,
            pollOptionId = "opt-1",
        )

        val json = gson.toJson(replyParameters)

        assertThat(json).contains("\"checklist_task_id\":3")
        assertThat(json).contains("\"poll_option_id\":\"opt-1\"")
    }

    @Test
    fun `omits null fields entirely from the JSON output`() {
        val replyParameters = ReplyParameters(messageId = 42L)

        val json = gson.toJson(replyParameters)

        assertThat(json).doesNotContain("chat_id")
        assertThat(json).doesNotContain("quote")
        assertThat(json).doesNotContain("allow_sending_without_reply")
        assertThat(json).doesNotContain("checklist_task_id")
        assertThat(json).doesNotContain("poll_option_id")
    }
}
