package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatFullInfo
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatLocation
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Location
import com.github.kotlintelegrambot.entities.gifts.AcceptedGiftTypes
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

        val expectedGetChatResult = ChatFullInfo(
            id = -1001342283806,
            type = "supergroup",
            accentColorId = 0,
            maxReactionCount = 11,
            acceptedGiftTypes = AcceptedGiftTypes(
                unlimitedGifts = true,
                limitedGifts = true,
                uniqueGifts = true,
                premiumSubscription = false,
            ),
            title = "[Local Group] Test Telegram Bot Api",
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
            ),
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
                    "accent_color_id": 0,
                    "max_reaction_count": 11,
                    "accepted_gift_types": {
                        "unlimited_gifts": true,
                        "limited_gifts": true,
                        "unique_gifts": true,
                        "premium_subscription": false
                    },
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
                .setBody(getChatResponse),
        )
    }

    private companion object {
        const val ANY_CHAT_ID = 2351235L
    }
}
