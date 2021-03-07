package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.polls.Poll
import com.github.kotlintelegrambot.entities.polls.PollOption
import com.github.kotlintelegrambot.entities.polls.PollType
import com.github.kotlintelegrambot.testutils.apiMethodName
import com.github.kotlintelegrambot.testutils.decodedBody
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class StopPollIT : ApiClientIT() {

    @Test
    fun `api method name`() {
        givenStopPollSuccessfulResponse()

        sut.stopPoll(
            chatId = ANY_CHAT_ID,
            messageId = ANY_MESSAGE_ID,
            replyMarkup = null,
        )

        val request = mockWebServer.takeRequest()
        assertEquals("stopPoll", request.apiMethodName)
    }

    @Test
    fun `request body with mandatory parameters`() {
        givenStopPollSuccessfulResponse()

        sut.stopPoll(
            chatId = ANY_CHAT_ID,
            messageId = ANY_MESSAGE_ID,
            replyMarkup = null,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=${ANY_CHAT_ID.id}&message_id=$ANY_MESSAGE_ID"
        assertEquals(expectedRequestBody, request.decodedBody)
    }

    @Test
    fun `request body with all parameters`() {
        givenStopPollSuccessfulResponse()

        sut.stopPoll(
            chatId = ANY_CHAT_ID,
            messageId = ANY_MESSAGE_ID,
            replyMarkup = InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.Url(
                        text = ANY_TEXT,
                        url = ANY_URL,
                    )
                )
            ),
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=${ANY_CHAT_ID.id}" +
            "&message_id=$ANY_MESSAGE_ID" +
            "&reply_markup={\"inline_keyboard\":[[{\"text\":\"$ANY_TEXT\",\"url\":\"$ANY_URL\"}]]}"
        assertEquals(expectedRequestBody, request.decodedBody)
    }

    @Test
    fun `successful response result`() {
        givenStopPollSuccessfulResponse()

        val stopPollResult = sut.stopPoll(
            chatId = ANY_CHAT_ID,
            messageId = ANY_MESSAGE_ID,
            replyMarkup = null,
        )

        val expectedStopPollResult = Poll(
            id = 5920442882291925002,
            question = "Pizza with or without pineapple?",
            options = listOf(
                PollOption(
                    text = "With :(",
                    voterCount = 0,
                ),
                PollOption(
                    text = "Without :)",
                    voterCount = 0,
                )
            ),
            totalVoterCount = 0,
            isClosed = true,
            isAnonymous = false,
            type = PollType.REGULAR,
            allowsMultipleAnswers = false,
        )
        assertEquals(expectedStopPollResult, stopPollResult.get())
    }

    private fun givenStopPollSuccessfulResponse() {
        val stopPollResponse = """
            {
                "ok": true,
                "result": {
                    "id": "5920442882291925002",
                    "question": "Pizza with or without pineapple?",
                    "options": [
                        {
                            "text": "With :(",
                            "voter_count": 0
                        },
                        {
                            "text": "Without :)",
                            "voter_count": 0
                        }
                    ],
                    "total_voter_count": 0,
                    "is_closed": true,
                    "is_anonymous": false,
                    "type": "regular",
                    "allows_multiple_answers": false
                }
            }
        """.trimIndent()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(stopPollResponse)
        )
    }

    private companion object {
        val ANY_CHAT_ID = ChatId.fromId(1412414L)
        const val ANY_MESSAGE_ID = 15124124124L
        const val ANY_TEXT = "banger"
        const val ANY_URL = "https://www.telegram.bot"
    }
}
