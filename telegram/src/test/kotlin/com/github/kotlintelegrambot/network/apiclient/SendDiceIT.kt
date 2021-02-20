package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.entities.dice.Dice
import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SendDiceIT : ApiClientIT() {

    @Test
    fun `sendDice only with mandatory parameters`() {
        givenAnySendDiceResponse()

        sut.sendDice(ChatId.fromId(ANY_CHAT_ID))

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendDice with dice emoji`() {
        givenAnySendDiceResponse()

        sut.sendDice(ChatId.fromId(ANY_CHAT_ID), emoji = DiceEmoji.Dice)

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&emoji=🎲"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendDice with dartboard emoji`() {
        givenAnySendDiceResponse()

        sut.sendDice(ChatId.fromId(ANY_CHAT_ID), emoji = DiceEmoji.Dartboard)

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&emoji=🎯"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendDice with basketball emoji`() {
        givenAnySendDiceResponse()

        sut.sendDice(ChatId.fromId(ANY_CHAT_ID), emoji = DiceEmoji.Basketball)

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&emoji=🏀"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendDice with football emoji`() {
        givenAnySendDiceResponse()

        sut.sendDice(ChatId.fromId(ANY_CHAT_ID), emoji = DiceEmoji.Football)

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&emoji=⚽️"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendDice with slot machine emoji`() {
        givenAnySendDiceResponse()

        sut.sendDice(ChatId.fromId(ANY_CHAT_ID), emoji = DiceEmoji.SlotMachine)

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&emoji=🎰"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendDice with bowling emoji`() {
        givenAnySendDiceResponse()

        sut.sendDice(ChatId.fromId(ANY_CHAT_ID), emoji = DiceEmoji.Bowling)

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&emoji=🎳"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendDice with all the optional parameters`() {
        givenAnySendDiceResponse()

        sut.sendDice(
            ChatId.fromId(ANY_CHAT_ID),
            emoji = DiceEmoji.Dartboard,
            disableNotification = DISABLE_NOTIFICATION,
            replyToMessageId = ANY_MESSAGE_ID
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID" +
            "&emoji=🎯" +
            "&disable_notification=$DISABLE_NOTIFICATION" +
            "&reply_to_message_id=$ANY_MESSAGE_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendDice response is correctly returned`() {
        givenAnySendDiceResponse()

        val sendDiceResult = sut.sendDice(ChatId.fromId(ANY_CHAT_ID), emoji = DiceEmoji.Dartboard)

        val expectedMessage = Message(
            messageId = 56,
            from = User(
                id = 482352699,
                isBot = true,
                firstName = "foo",
                username = "bar"
            ),
            chat = Chat(
                id = -1001287972005,
                title = "Test Telegram Bot API",
                type = "supergroup"
            ),
            date = 1590313567,
            dice = Dice(
                emoji = DiceEmoji.Dartboard,
                value = 6
            )
        )
        assertEquals(expectedMessage, sendDiceResult.getOrNull())
    }

    private fun givenAnySendDiceResponse() {
        val sendDiceResponse = """
            {
                "ok": true,
                "result": {
                    "message_id": 56,
                    "from": {
                        "id": 482352699,
                        "is_bot": true,
                        "first_name": "foo",
                        "username": "bar"
                    },
                    "chat": {
                        "id": -1001287972005,
                        "title": "Test Telegram Bot API",
                        "type": "supergroup"
                    },
                    "date": 1590313567,
                    "dice": {
                        "emoji": "🎯",
                        "value": 6
                    }
                }
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(sendDiceResponse)
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 2351353153L
        const val DISABLE_NOTIFICATION = true
        const val ANY_MESSAGE_ID = 3152321342L
    }
}
