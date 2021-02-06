package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatMember
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.testutils.apiMethodName
import com.github.kotlintelegrambot.testutils.queryParams
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class GetChatAdministratorsIT : ApiClientIT() {

    @Test
    fun `request is properly sent`() {
        givenASuccessfulGetChatMembersResponse()

        sut.getChatAdministrators(ChatId.fromId(ANY_CHAT_ID))

        val request = mockWebServer.takeRequest()
        assertEquals("getChatAdministrators", request.apiMethodName)
        assertEquals("chat_id=$ANY_CHAT_ID", request.queryParams)
    }

    @Test
    fun `successful response is returned correctly`() {
        givenASuccessfulGetChatMembersResponse()

        val getChatAdministratorsResult = sut.getChatAdministrators(
            chatId = ChatId.fromId(ANY_CHAT_ID)
        ).getOrNull()

        val expectedChatAdministrators = listOf(
            ChatMember(
                user = User(
                    id = 482352639,
                    isBot = true,
                    firstName = "test",
                    username = "testBot",
                ),
                status = "administrator",
                canBeEdited = false,
                canChangeInfo = true,
                canDeleteMessages = true,
                canInviteUsers = true,
                canRestrictMembers = true,
                canPinMessages = true,
                canPromoteMembers = true,
                isAnonymous = false,
            ),
            ChatMember(
                user = User(
                    id = 187395279,
                    isBot = false,
                    firstName = "Tyrion",
                    lastName = "Lannister",
                    username = "dwarfing",
                    languageCode = "en",
                ),
                status = "creator",
                isAnonymous = false,
            )
        )
        assertEquals(expectedChatAdministrators, getChatAdministratorsResult)
    }

    private fun givenASuccessfulGetChatMembersResponse() {
        val getChaMembersResponseBody = """
            {
                "ok": true,
                "result": [
                    {
                        "user": {
                            "id": 482352639,
                            "is_bot": true,
                            "first_name": "test",
                            "username": "testBot"
                        },
                        "status": "administrator",
                        "can_be_edited": false,
                        "can_change_info": true,
                        "can_delete_messages": true,
                        "can_invite_users": true,
                        "can_restrict_members": true,
                        "can_pin_messages": true,
                        "can_promote_members": true,
                        "is_anonymous": false
                    },
                    {
                        "user": {
                            "id": 187395279,
                            "is_bot": false,
                            "first_name": "Tyrion",
                            "last_name": "Lannister",
                            "username": "dwarfing",
                            "language_code": "en"
                        },
                        "status": "creator",
                        "is_anonymous": false
                    }
                ]
            }
        """.trimIndent()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(getChaMembersResponseBody)
        )
    }

    private companion object {
        const val ANY_CHAT_ID = 35235235L
    }
}
