package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatInviteLink
import com.github.kotlintelegrambot.entities.ChatMember
import com.github.kotlintelegrambot.entities.ChatMemberUpdated
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.InlineQuery
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.testutils.apiMethodName
import com.github.kotlintelegrambot.testutils.queryParams
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class GetUpdatesIT : ApiClientIT() {

    @Test
    fun `api method name`() {
        givenAnyGetUpdatesResponse()

        sut.getUpdates(null, null, null, null)

        val request = mockWebServer.takeRequest()
        assertEquals("getUpdates", request.apiMethodName)
    }

    @Test
    fun `query parameters when called without parameters`() {
        givenAnyGetUpdatesResponse()

        sut.getUpdates(null, null, null, null)

        val request = mockWebServer.takeRequest()
        assertNull(request.queryParams)
    }

    @Test
    fun `query parameters when called with all the parameters`() {
        givenAnyGetUpdatesResponse()

        sut.getUpdates(
            offset = ANY_OFFSET,
            limit = ANY_LIMIT,
            timeout = ANY_TIMEOUT,
            allowedUpdates = ANY_ALLOWED_UPDATES,
        )

        val request = mockWebServer.takeRequest()
        val expectedQueryParameters = "offset=$ANY_OFFSET" +
            "&limit=$ANY_LIMIT" +
            "&timeout=$ANY_TIMEOUT" +
            "&allowed_updates=%5B%22message%22%2C%22edited_channel_post%22%2C%22callback_query%22%5D"
        assertEquals(expectedQueryParameters, request.queryParams)
    }

    @Test
    fun `getUpdates returning an update with callback query containing inline keyboard buttons`() {
        givenGetUpdatesResponse(
            """
            {
                "ok": true,
                "result": [
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
                ]
            }
            """.trimIndent(),
        )

        val getUpdatesResult = sut.getUpdates(null, null, null, null)

        val expectedUpdates = listOf(
            Update(
                updateId = 1,
                callbackQuery = CallbackQuery(
                    id = "1",
                    from = User(
                        id = 1,
                        isBot = false,
                        firstName = "TestName",
                        username = "testname",
                        languageCode = "de",
                    ),
                    message = Message(
                        messageId = 1,
                        from = User(
                            id = 1,
                            isBot = true,
                            firstName = "testbot",
                            username = "testbot",
                        ),
                        chat = Chat(
                            id = 1,
                            firstName = "TestName",
                            username = "testname",
                            type = "private",
                        ),
                        date = 1606317592,
                        text = "Hello, inline buttons!",
                        replyMarkup = InlineKeyboardMarkup.create(
                            listOf(
                                InlineKeyboardButton.CallbackData(
                                    text = "Test Inline Button",
                                    callbackData = "testButton",
                                ),
                            ),
                            listOf(
                                InlineKeyboardButton.CallbackData(
                                    text = "Show alert",
                                    callbackData = "showAlert",
                                ),
                            ),
                        ),
                    ),
                    chatInstance = "1",
                    data = "showAlert",
                ),
            ),
        )
        assertEquals(expectedUpdates, getUpdatesResult.getOrNull())
    }

    @Test
    fun `getUpdates with a channel post containing sender chat`() {
        givenGetUpdatesResponse(
            """
                {
                    "ok": true,
                    "result": [
                        {
                            "update_id": 132059007,
                            "channel_post": {
                                "message_id": 18,
                                "sender_chat": {
                                    "id": -1001367429635,
                                    "title": "[Channel] Test Telegram Bot",
                                    "username": "testtelegrambotapi",
                                    "type": "channel"
                                },
                                "chat": {
                                    "id": -1001367429635,
                                    "title": "[Channel] Test Telegram Bot",
                                    "username": "testtelegrambotapi",
                                    "type": "channel"
                                },
                                "date": 1612631280,
                                "text": "Test"
                            }
                        }
                    ]
                }
            """.trimIndent(),
        )

        val getUpdatesResult = sut.getUpdates(null, null, null, null)

        val expectedGetUpdatesResult = listOf(
            Update(
                updateId = 132059007,
                channelPost = Message(
                    messageId = 18,
                    senderChat = Chat(
                        id = -1001367429635,
                        title = "[Channel] Test Telegram Bot",
                        username = "testtelegrambotapi",
                        type = "channel",
                    ),
                    chat = Chat(
                        id = -1001367429635,
                        title = "[Channel] Test Telegram Bot",
                        username = "testtelegrambotapi",
                        type = "channel",
                    ),
                    date = 1612631280,
                    text = "Test",
                ),
            ),
        )
        assertEquals(expectedGetUpdatesResult, getUpdatesResult.getOrNull())
    }

    @Test
    fun `getUpdates with a message containing a date after 03h14m07s UTC on 19 January 2038`() {
        givenGetUpdatesResponse(
            """
                {
                    "ok": true,
                    "result": [
                        {
                            "update_id": 132059007,
                            "message": {
                                "message_id": 18,
                                "chat": {
                                    "id": -1001367429635,
                                    "title": "[Channel] Test Telegram Bot",
                                    "username": "testtelegrambotapi",
                                    "type": "channel"
                                },
                                "date": 2147483648,
                                "text": "Test"
                            }
                        }
                    ]
                }
            """.trimIndent(),
        )

        val getUpdatesResult = sut.getUpdates(null, null, null, null)

        val expectedGetUpdatesResult = listOf(
            Update(
                updateId = 132059007,
                message = Message(
                    messageId = 18,
                    chat = Chat(
                        id = -1001367429635,
                        title = "[Channel] Test Telegram Bot",
                        username = "testtelegrambotapi",
                        type = "channel",
                    ),
                    date = 2147483648,
                    text = "Test",
                ),
            ),
        )
        assertEquals(expectedGetUpdatesResult, getUpdatesResult.get())
    }

    @Test
    fun `getUpdates with a bot ban event`() {
        givenGetUpdatesResponse(
            """
                {
                    "ok": true,
                    "result": [
                        {
                            "update_id": 93885989,
                            "my_chat_member": {
                                "chat": {
                                    "id": 187395179,
                                    "first_name": "Sheldon",
                                    "username": "shelly",
                                    "type": "private"
                                },
                                "from": {
                                    "id": 187395179,
                                    "is_bot": false,
                                    "first_name": "Sheldon",
                                    "username": "shelly",
                                    "language_code": "en"
                                },
                                "date": 1678450453,
                                "old_chat_member": {
                                    "user": {
                                        "id": 1,
                                        "is_bot": true,
                                        "first_name": "testbot",
                                        "username": "testbot"
                                    },
                                    "status": "member"
                                },
                                "new_chat_member": {
                                    "user": {
                                        "id": 1,
                                        "is_bot": true,
                                        "first_name": "testbot",
                                        "username": "testbot"
                                    },
                                    "status": "kicked",
                                    "until_date": 0
                                },
                                "invite_link": {
                                    "invite_link" : "https://t.me/joincha",
                                    "creator" : {
                                        "id": 187395179,
                                        "is_bot": false,
                                        "first_name": "Sheldon",
                                        "username": "shelly",
                                        "language_code": "en"
                                    },
                                    "creates_join_request" : true,
                                    "is_primary" : true,
                                    "is_revoked" : true,
                                    "name" : "some name",
                                    "expire_date" : 123456789,
                                    "member_limit" : 100,
                                    "pending_join_request_count" : 55
                                },
                                "via_chat_folder_invite_link": true
                            }
                        }
                    ]
                }
            """.trimIndent(),
        )

        val getUpdatesResult = sut.getUpdates(null, null, null, null)

        val expectedGetUpdatesResult = listOf(
            Update(
                updateId = 93885989,
                myChatMember = ChatMemberUpdated(
                    chat = Chat(
                        id = 187395179,
                        firstName = "Sheldon",
                        username = "shelly",
                        type = "private",
                    ),
                    from = User(
                        id = 187395179,
                        isBot = false,
                        firstName = "Sheldon",
                        username = "shelly",
                        languageCode = "en",
                    ),
                    date = 1678450453,
                    oldChatMember = ChatMember(
                        user = User(
                            id = 1,
                            isBot = true,
                            firstName = "testbot",
                            username = "testbot",
                        ),
                        status = "member",
                    ),
                    newChatMember = ChatMember(
                        user = User(
                            id = 1,
                            isBot = true,
                            firstName = "testbot",
                            username = "testbot",
                        ),
                        status = "kicked",
                        untilDate = 0,
                    ),
                    inviteLink = ChatInviteLink(
                        inviteLink = "https://t.me/joincha",
                        creator = User(
                            id = 187395179,
                            isBot = false,
                            firstName = "Sheldon",
                            username = "shelly",
                            languageCode = "en",
                        ),
                        createsJoinRequest = true,
                        isPrimary = true,
                        isRevoked = true,
                        name = "some name",
                        expireDate = 123456789,
                        memberLimit = 100,
                        pendingJoinRequestCount = 55,
                    ),
                    viaChatFolderInviteLink = true,
                ),
            ),
        )
        assertEquals(expectedGetUpdatesResult, getUpdatesResult.get())
    }

    @Test
    fun `getUpdates with inline queries`() {
        givenGetUpdatesResponse(
            """
{
    "ok": true,
    "result": [
        {
            "update_id": 917440351,
            "inline_query": {
                "id": "804856167979007700",
                "from": {
                    "id": 187395179,
                    "is_bot": false,
                    "first_name": "Sheldon",
                    "last_name": "Cooper",
                    "username": "shelly",
                    "language_code": "en"
                },
                "chat_type": "sender",
                "query": "",
                "offset": ""
            }
        },
        {
            "update_id": 917440352,
            "inline_query": {
                "id": "804856169188869353",
                "from": {
                    "id": 187395179,
                    "is_bot": false,
                    "first_name": "Sheldon",
                    "last_name": "Cooper",
                    "username": "shelly",
                    "language_code": "en"
                },
                "chat_type": "supergroup",
                "query": "h",
                "offset": ""
            }
        },
        {
            "update_id": 917440353,
            "inline_query": {
                "id": "804856169188869354",
                "from": {
                    "id": 187395179,
                    "is_bot": false,
                    "first_name": "Sheldon",
                    "last_name": "Cooper",
                    "username": "shelly",
                    "language_code": "en"
                },
                "query": "hi",
                "offset": ""
            }
        }
    ]
}
            """.trimIndent(),
        )

        val getUpdatesResult = sut.getUpdates(
            offset = null,
            limit = null,
            timeout = null,
            allowedUpdates = null,
        )

        val user = User(
            id = 187395179,
            isBot = false,
            firstName = "Sheldon",
            lastName = "Cooper",
            username = "shelly",
            languageCode = "en",
        )
        val expectedGetUpdatesResult = listOf(
            Update(
                updateId = 917440351,
                inlineQuery = InlineQuery(
                    id = "804856167979007700",
                    from = user,
                    chatType = InlineQuery.ChatType.SENDER,
                    query = "",
                    offset = "",
                ),
            ),
            Update(
                updateId = 917440352,
                inlineQuery = InlineQuery(
                    id = "804856169188869353",
                    from = user,
                    chatType = InlineQuery.ChatType.SUPERGROUP,
                    query = "h",
                    offset = "",
                ),
            ),
            Update(
                updateId = 917440353,
                inlineQuery = InlineQuery(
                    id = "804856169188869354",
                    from = user,
                    query = "hi",
                    offset = "",
                ),
            ),
        )
        assertEquals(expectedGetUpdatesResult, getUpdatesResult.getOrNull())
    }

    private fun givenGetUpdatesResponse(getUpdatesResponseJson: String) {
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(getUpdatesResponseJson)
        mockWebServer.enqueue(mockedResponse)
    }

    private fun givenAnyGetUpdatesResponse() {
        val responseBody = """
            {
                "ok": true,
                "result": []
            }
        """.trimIndent()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody),
        )
    }

    private companion object {
        const val ANY_OFFSET = 234234L
        const val ANY_TIMEOUT = 3244
        const val ANY_LIMIT = 12412
        val ANY_ALLOWED_UPDATES = listOf("message", "edited_channel_post", "callback_query")
    }
}
