package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.entities.polls.Poll
import com.github.kotlintelegrambot.entities.polls.PollOption
import com.github.kotlintelegrambot.entities.polls.PollType.REGULAR
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SendPollIT : ApiClientIT() {

    @Test
    fun `sendPoll with chat id and only the mandatory parameters is correctly sent`() {
        givenAnySendPollResponse()

        sut.sendPoll(ChatId.fromId(ANY_CHAT_ID), ANY_QUESTION, ANY_POLL_OPTIONS)

        val request = mockWebServer.takeRequest()
        val expectedRequestBody =
            "chat_id=$ANY_CHAT_ID&question=$ANY_QUESTION&options=${ANY_POLL_OPTIONS.joinToString(",", "[", "]", transform = { "\"$it\"" })}"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendPoll with channel username and only the mandatory parameters is correctly sent`() {
        givenAnySendPollResponse()

        sut.sendPoll(ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME), ANY_QUESTION, ANY_POLL_OPTIONS)

        val request = mockWebServer.takeRequest()
        val expectedRequestBody =
            "chat_id=$ANY_CHANNEL_USERNAME&question=$ANY_QUESTION&options=${ANY_POLL_OPTIONS.joinToString(",", "[", "]", transform = { "\"$it\"" })}"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendPoll with the mandatory parameters and some of the optionals is correctly sent`() {
        givenAnySendPollResponse()

        sut.sendPoll(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            question = ANY_QUESTION,
            options = ANY_POLL_OPTIONS,
            allowsMultipleAnswers = ALLOWS_MULTIPLE_ANSWERS,
            openPeriod = ANY_OPEN_PERIOD
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody =
            "chat_id=$ANY_CHANNEL_USERNAME" +
                "&question=$ANY_QUESTION" +
                "&options=${ANY_POLL_OPTIONS.joinToString(",", "[", "]", transform = { "\"$it\"" })}" +
                "&allows_multiple_answers=$ALLOWS_MULTIPLE_ANSWERS" +
                "&open_period=$ANY_OPEN_PERIOD"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendPoll with all the parameters is correctly sent`() {
        givenAnySendPollResponse()

        sut.sendPoll(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            question = ANY_QUESTION,
            options = ANY_POLL_OPTIONS,
            isAnonymous = IS_ANONYMOUS,
            type = REGULAR,
            allowsMultipleAnswers = ALLOWS_MULTIPLE_ANSWERS,
            correctOptionId = ANY_OPTION_ID,
            explanation = ANY_EXPLANATION,
            explanationParseMode = MARKDOWN,
            openPeriod = ANY_OPEN_PERIOD,
            isClosed = IS_CLOSED,
            disableNotification = DO_NOT_DISABLE_NOTIFICATIONS,
            replyToMessageId = ANY_MESSAGE_ID
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody =
            "chat_id=$ANY_CHAT_ID" +
                "&question=$ANY_QUESTION" +
                "&options=${ANY_POLL_OPTIONS.joinToString(",", "[", "]", transform = { "\"$it\"" })}" +
                "&is_anonymous=$IS_ANONYMOUS" +
                "&type=regular" +
                "&allows_multiple_answers=$ALLOWS_MULTIPLE_ANSWERS" +
                "&correct_option_id=$ANY_OPTION_ID" +
                "&explanation=$ANY_EXPLANATION" +
                "&explanation_parse_mode=Markdown" +
                "&open_period=$ANY_OPEN_PERIOD" +
                "&is_closed=$IS_CLOSED" +
                "&disable_notification=$DO_NOT_DISABLE_NOTIFICATIONS" +
                "&reply_to_message_id=$ANY_MESSAGE_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendPoll response is returned correctly`() {
        givenAnySendPollResponse()

        val sendPollResult = sut.sendPoll(ChatId.fromId(ANY_CHAT_ID), ANY_QUESTION, ANY_POLL_OPTIONS)

        val expectedMessage = Message(
            messageId = 9,
            from = User(
                id = 482352699,
                isBot = true,
                firstName = "ruka",
                username = "rukaBot"
            ),
            chat = Chat(
                id = -1001286972005,
                title = "Test Telegram Bot API",
                type = "supergroup"
            ),
            date = 1589629064,
            poll = Poll(
                id = 5906677791281119236,
                question = "World war III, what do you prefer?",
                options = listOf(
                    PollOption(text = "tabs", voterCount = 0),
                    PollOption(text = "spaces", voterCount = 1)
                ),
                totalVoterCount = 1,
                isClosed = false,
                isAnonymous = true,
                type = REGULAR,
                allowsMultipleAnswers = false
            )
        )
        assertEquals(expectedMessage, sendPollResult.get())
    }

    private fun givenAnySendPollResponse() {
        val sendPollResponse = """
            {
                "ok": true,
                "result": {
                    "message_id": 9,
                    "from": {
                        "id": 482352699,
                        "is_bot": true,
                        "first_name": "ruka",
                        "username": "rukaBot"
                    },
                    "chat": {
                        "id": -1001286972005,
                        "title": "Test Telegram Bot API",
                        "type": "supergroup"
                    },
                    "date": 1589629064,
                    "poll": {
                        "id": "5906677791281119236",
                        "question": "World war III, what do you prefer?",
                        "options": [
                            {
                                "text": "tabs",
                                "voter_count": 0
                            },
                            {
                                "text": "spaces",
                                "voter_count": 1
                            }
                        ],
                        "total_voter_count": 1,
                        "is_closed": false,
                        "is_anonymous": true,
                        "type": "regular",
                        "allows_multiple_answers": false
                    }
                }
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(sendPollResponse)
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 12412342L
        const val ANY_CHANNEL_USERNAME = "@polly"
        const val ANY_QUESTION = "World war III, what do you prefer?"
        val ANY_POLL_OPTIONS = listOf("tabs", "spaces")
        const val ANY_OPEN_PERIOD = 600
        const val ALLOWS_MULTIPLE_ANSWERS = true
        const val IS_ANONYMOUS = true
        const val ANY_OPTION_ID = 2
        const val ANY_EXPLANATION = "Tabs are better than spaces. Because of yes."
        const val IS_CLOSED = true
        const val DO_NOT_DISABLE_NOTIFICATIONS = false
        const val ANY_MESSAGE_ID = 2314314L
    }
}
