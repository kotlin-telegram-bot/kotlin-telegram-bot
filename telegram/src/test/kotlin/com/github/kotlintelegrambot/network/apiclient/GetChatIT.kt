package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatLocation
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Location
import com.github.kotlintelegrambot.testutils.apiMethodName
import com.github.kotlintelegrambot.testutils.queryParams
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class GetChatIT : ApiClientIT() {

    @Test
    fun `correct request`() {
        givenAnyGetChatSuccessfulResponse()

        sut.getChat(ChatId.fromId(ANY_CHAT_ID))

        val request = mockWebServer.takeRequest()
        assertEquals("getChat", request.apiMethodName)
        assertEquals("chat_id=$ANY_CHAT_ID", request.queryParams)
    }

    @Test
    fun `correct response`() {
        givenAnyGetChatSuccessfulResponse()

        val getChatResult = sut.getChat(ChatId.fromId(ANY_CHAT_ID))

        val expectedGetChatResult = Chat(
            id = -1001342283806,
            title = "[Local Group] Test Telegram Bot Api",
            type = "supergroup",
            permissions = ChatPermissions(
                canSendMessages = true,
                canSendMediaMessages = true,
                canSendPolls = true,
                canSendOtherMessages = true,
                canAddWebPagePreviews = true,
                canChangeInfo = false,
                canInviteUsers = false,
                canPinMessages = false,
            ),
            location = ChatLocation(
                location = Location(
                    latitude = 20.425537f,
                    longitude = -3.604971f,
                ),
                address = "Mordor Street, Madrid, Spain",
            )
        )
        assertEquals(expectedGetChatResult, getChatResult.getOrNull())
    }

    private fun givenAnyGetChatSuccessfulResponse() {
        val getChatResponse = """
            {
                "ok": true,
                "result": {
                    "id": -1001342283806,
                    "title": "[Local Group] Test Telegram Bot Api",
                    "type": "supergroup",
                    "permissions": {
                        "can_send_messages": true,
                        "can_send_media_messages": true,
                        "can_send_polls": true,
                        "can_send_other_messages": true,
                        "can_add_web_page_previews": true,
                        "can_change_info": false,
                        "can_invite_users": false,
                        "can_pin_messages": false
                    },
                    "location": {
                        "location": {
                            "latitude": 20.425537,
                            "longitude": -3.604971
                        },
                        "address": "Mordor Street, Madrid, Spain"
                    }
                }
            }
        """.trimIndent()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(getChatResponse)
        )
    }

    private companion object {
        const val ANY_CHAT_ID = 2351235L
    }
}
