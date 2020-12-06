package com.github.kotlintelegrambot

import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton.CallbackData
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test

class UpdateMapperTest {

    private val sut = UpdateMapper(
        gson = GsonFactory.createForApiClient()
    )

    @Test
    fun `update with callback query containing an inline keyboard is mapped correctly`() {
        val updateJson = """
            {
                "update_id": 1,
                "callback_query": {
                    "id": "1",
                    "from": {
                        "id": 1,
                        "is_bot": false,
                        "first_name": "TestName",
                        "username": "testname",
                        "language_code": "de"
                    },
                    "message": {
                        "message_id": 1,
                        "from": {
                            "id": 1,
                            "is_bot": true,
                            "first_name": "testbot",
                            "username": "testbot"
                        },
                        "chat": {
                            "id": 1,
                            "first_name": "TestName",
                            "username": "testname",
                            "type": "private"
                        },
                        "date": 1606317592,
                        "text": "Hello, inline buttons!",
                        "reply_markup": {
                            "inline_keyboard": [
                                [
                                    {
                                        "text": "Test Inline Button",
                                        "callback_data": "testButton"
                                    }
                                ],
                                [
                                    {
                                        "text": "Show alert",
                                        "callback_data": "showAlert"
                                    }
                                ]
                            ]
                        }
                    },
                    "chat_instance": "1",
                    "data": "showAlert"
                }
            }
        """.trimIndent()

        val actualUpdate = sut.jsonToUpdate(updateJson)

        val expectedUpdate = Update(
            updateId = 1,
            callbackQuery = CallbackQuery(
                id = "1",
                from = User(
                    id = 1,
                    isBot = false,
                    firstName = "TestName",
                    username = "testname",
                    languageCode = "de"
                ),
                message = Message(
                    messageId = 1,
                    from = User(
                        id = 1,
                        isBot = true,
                        firstName = "testbot",
                        username = "testbot"
                    ),
                    chat = Chat(
                        id = 1,
                        firstName = "TestName",
                        username = "testname",
                        type = "private"
                    ),
                    date = 1606317592,
                    text = "Hello, inline buttons!",
                    replyMarkup = InlineKeyboardMarkup.create(
                        listOf(
                            CallbackData(
                                text = "Test Inline Button",
                                callbackData = "testButton"
                            )
                        ),
                        listOf(
                            CallbackData(
                                text = "Show alert",
                                callbackData = "showAlert"
                            )
                        )
                    )
                ),
                chatInstance = "1",
                data = "showAlert"
            )
        )
        assertEquals(expectedUpdate, actualUpdate)
    }
}
