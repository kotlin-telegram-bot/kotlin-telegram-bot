package com.github.kotlintelegrambot.network.apiclient

import anyChat
import anyGame
import anyMessage
import anyPhotoSize
import anyUser
import com.github.kotlintelegrambot.entities.CallbackGame
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.network.Response
import com.github.kotlintelegrambot.testutils.decode
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SendGameIT : ApiClientIT() {

    @Test
    fun `#sendGame with all parameters creates request correctly`() {
        givenAnySendGameResponse()

        sut.sendGame(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            gameShortName = ANY_GAME_NAME,
            disableNotification = true,
            replyToMessageId = REPLY_TO_MESSAGE_ID,
            allowSendingWithoutReply = true,
            replyMarkup = REPLY_MARKUP_WITH_1ST_BUTTON_LAUNCH
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID" +
            "&game_short_name=$ANY_GAME_NAME" +
            "&disable_notification=true" +
            "&reply_to_message_id=$REPLY_TO_MESSAGE_ID" +
            "&allow_sending_without_reply=true" +
            "&reply_markup=${gson.toJson(REPLY_MARKUP_WITH_1ST_BUTTON_LAUNCH)}"

        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `#sendGame with required parameters returns response correctly`() {
        givenAnySendGameResponse()

        val sendGameResponse = sut.sendGame(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            gameShortName = ANY_GAME_NAME
        )

        assertEquals(anyGameMessage.toString().trim(), sendGameResponse.get().toString().trim())
    }

    private fun givenAnySendGameResponse() {
        val sendGameResponse = Response<Message>(
            ok = true,
            result = anyGameMessage
        )

        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(gson.toJson(sendGameResponse))
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        val gson = Gson()
        const val ANY_CHAT_ID = 2351353153L
        const val ANY_GAME_NAME = "game_name"
        const val REPLY_TO_MESSAGE_ID = 32235235L

        val REPLY_MARKUP_WITH_1ST_BUTTON_LAUNCH = InlineKeyboardMarkup.createSingleButton(
            InlineKeyboardButton.CallbackGameButtonType(
                text = "Play gameTest",
                callbackGame = CallbackGame()
            )
        )

        val anyGameMessage = anyMessage(
            from = anyUser(),
            chat = anyChat(
                id = ANY_CHAT_ID
            ),
            game = anyGame(
                photos = listOf(anyPhotoSize(), anyPhotoSize())
            ),
            replyMarkup = REPLY_MARKUP_WITH_1ST_BUTTON_LAUNCH
        )
    }
}
