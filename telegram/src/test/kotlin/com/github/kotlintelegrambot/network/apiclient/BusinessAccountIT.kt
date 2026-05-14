package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InputProfilePhoto
import com.github.kotlintelegrambot.entities.gifts.AcceptedGiftTypes
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for Bot API 7.2 / 9.0 business-account methods (the bot acting on behalf
 * of a connected business account).
 */
class BusinessAccountIT : ApiClientIT() {

    @Test
    fun `readBusinessMessage posts business_connection_id, chat_id and message_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.readBusinessMessage(businessConnectionId = "conn-abc", chatId = ChatId.fromId(11L), messageId = 7L)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/readBusinessMessage"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("chat_id=11"))
        assertEquals(true, body.contains("message_id=7"))
    }

    @Test
    fun `deleteBusinessMessages posts business_connection_id and serialized message_ids`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.deleteBusinessMessages(businessConnectionId = "conn-abc", messageIds = listOf(1L, 2L, 3L))

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/deleteBusinessMessages"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("message_ids=[1,2,3]"))
    }

    @Test
    fun `setBusinessAccountName posts business_connection_id, first_name and last_name`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setBusinessAccountName(businessConnectionId = "conn-abc", firstName = "Alice", lastName = "Bot")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setBusinessAccountName"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("first_name=Alice"))
        assertEquals(true, body.contains("last_name=Bot"))
    }

    @Test
    fun `setBusinessAccountUsername posts business_connection_id and username`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setBusinessAccountUsername(businessConnectionId = "conn-abc", username = "alicebot")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setBusinessAccountUsername"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("username=alicebot"))
    }

    @Test
    fun `setBusinessAccountBio posts business_connection_id and bio`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setBusinessAccountBio(businessConnectionId = "conn-abc", bio = "Hi!")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setBusinessAccountBio"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("bio=Hi!"))
    }

    @Test
    fun `setBusinessAccountProfilePhoto posts business_connection_id and serialized photo`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setBusinessAccountProfilePhoto(
            businessConnectionId = "conn-abc",
            photo = InputProfilePhoto.Static(photo = "file-id"),
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setBusinessAccountProfilePhoto"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("photo="))
        assertEquals(true, body.contains("\"type\":\"static\""))
        assertEquals(true, body.contains("\"photo\":\"file-id\""))
    }

    @Test
    fun `removeBusinessAccountProfilePhoto posts business_connection_id and is_public`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.removeBusinessAccountProfilePhoto(businessConnectionId = "conn-abc", isPublic = true)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/removeBusinessAccountProfilePhoto"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("is_public=true"))
    }

    @Test
    fun `setBusinessAccountGiftSettings posts show_gift_button and serialized accepted_gift_types`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setBusinessAccountGiftSettings(
            businessConnectionId = "conn-abc",
            showGiftButton = true,
            acceptedGiftTypes = AcceptedGiftTypes(
                unlimitedGifts = true,
                limitedGifts = true,
                uniqueGifts = false,
                premiumSubscription = false,
            ),
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setBusinessAccountGiftSettings"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("show_gift_button=true"))
        assertEquals(true, body.contains("accepted_gift_types="))
        assertEquals(true, body.contains("\"unlimited_gifts\":true"))
        assertEquals(true, body.contains("\"unique_gifts\":false"))
    }

    @Test
    fun `getBusinessAccountStarBalance posts business_connection_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":{"amount":0}}"""))

        sut.getBusinessAccountStarBalance(businessConnectionId = "conn-abc")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/getBusinessAccountStarBalance"))
        assertEquals("business_connection_id=conn-abc", body)
    }

    @Test
    fun `transferBusinessAccountStars posts business_connection_id and star_count`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.transferBusinessAccountStars(businessConnectionId = "conn-abc", starCount = 100)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/transferBusinessAccountStars"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("star_count=100"))
    }

    @Test
    fun `getBusinessAccountGifts posts business_connection_id and limit`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":{"total_count":0,"gifts":[]}}"""),
        )

        sut.getBusinessAccountGifts(businessConnectionId = "conn-abc", limit = 50)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/getBusinessAccountGifts"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("limit=50"))
    }

    @Test
    fun `convertGiftToStars posts business_connection_id and owned_gift_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.convertGiftToStars(businessConnectionId = "conn-abc", ownedGiftId = "og-1")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/convertGiftToStars"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("owned_gift_id=og-1"))
    }

    @Test
    fun `upgradeGift posts business_connection_id and owned_gift_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.upgradeGift(businessConnectionId = "conn-abc", ownedGiftId = "og-1")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/upgradeGift"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("owned_gift_id=og-1"))
    }

    @Test
    fun `transferGift posts business_connection_id, owned_gift_id and new_owner_chat_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.transferGift(businessConnectionId = "conn-abc", ownedGiftId = "og-1", newOwnerChatId = 999L)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/transferGift"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("owned_gift_id=og-1"))
        assertEquals(true, body.contains("new_owner_chat_id=999"))
    }
}
