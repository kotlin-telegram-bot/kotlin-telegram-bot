package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.inlinequeryresults.InlineQueryResult
import com.github.kotlintelegrambot.entities.inlinequeryresults.InputMessageContent
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for Bot API 8.0 gift methods.
 */
class GiftsIT : ApiClientIT() {
    @Test
    fun `getAvailableGifts issues a GET`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":{"gifts":[]}}"""))

        sut.getAvailableGifts()

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(true, request.path?.contains("/getAvailableGifts"))
    }

    @Test
    fun `sendGift posts gift_id, user_id and text`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.sendGift(giftId = "g-1", userId = 42L, text = "happy birthday")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/sendGift"))
        assertEquals(true, body.contains("gift_id=g-1"))
        assertEquals(true, body.contains("user_id=42"))
        assertEquals(true, body.contains("text=happy birthday") || body.contains("text=happy+birthday"))
    }

    @Test
    fun `giftPremiumSubscription posts user_id, month_count and star_count`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.giftPremiumSubscription(userId = 42L, monthCount = 3, starCount = 500)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/giftPremiumSubscription"))
        assertEquals(true, body.contains("user_id=42"))
        assertEquals(true, body.contains("month_count=3"))
        assertEquals(true, body.contains("star_count=500"))
    }

    @Test
    fun `setUserEmojiStatus posts user_id and emoji_status_custom_emoji_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setUserEmojiStatus(userId = 42L, emojiStatusCustomEmojiId = "5234")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setUserEmojiStatus"))
        assertEquals(true, body.contains("user_id=42"))
        assertEquals(true, body.contains("emoji_status_custom_emoji_id=5234"))
    }

    @Test
    fun `savePreparedInlineMessage posts user_id, serialized result and allow_user_chats`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"ok":true,"result":{"id":"pim-1","expiration_date":1700000000}}""",
            ),
        )

        sut.savePreparedInlineMessage(
            userId = 42L,
            result =
                InlineQueryResult.Article(
                    id = "1",
                    title = "Hi",
                    inputMessageContent = InputMessageContent.Text(messageText = "body"),
                ),
            allowUserChats = true,
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/savePreparedInlineMessage"))
        assertEquals(true, body.contains("user_id=42"))
        assertEquals(true, body.contains("result="))
        assertEquals(true, body.contains("\"title\":\"Hi\""))
        assertEquals(true, body.contains("\"message_text\":\"body\""))
        assertEquals(true, body.contains("allow_user_chats=true"))
    }
}
