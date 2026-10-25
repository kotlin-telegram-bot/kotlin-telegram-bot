package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ChatInviteLinkIT : ApiClientIT() {
    @Test
    fun createChatInviteLinkParsesReturnedLink() {
        enqueueInviteLinkResponse()

        val response = sut.createChatInviteLink(ChatId.fromId(CHAT_ID), null, null, null, null).execute()
        val link = requireNotNull(response.body()?.result)

        assertEquals("https://t.me/+example", link.inviteLink)
        assertEquals(321L, link.creator.id)
        assertFalse(link.isPrimary)
        assertEquals("/createChatInviteLink", mockWebServer.takeRequest().path?.substringAfterLast("bot"))
    }

    @Test
    fun editChatInviteLinkParsesEditedLink() {
        enqueueInviteLinkResponse()

        val response = sut.editChatInviteLink(ChatId.fromId(CHAT_ID), "https://t.me/+example").execute()
        val link = requireNotNull(response.body()?.result)

        assertEquals("https://t.me/+example", link.inviteLink)
        assertFalse(link.isRevoked)
        assertEquals("/editChatInviteLink", mockWebServer.takeRequest().path?.substringAfterLast("bot"))
    }

    @Test
    fun revokeChatInviteLinkParsesRevokedLink() {
        enqueueInviteLinkResponse(revoked = true)

        val response = sut.revokeChatInviteLink(ChatId.fromId(CHAT_ID), "https://t.me/+example").execute()
        val link = requireNotNull(response.body()?.result)

        assertEquals("https://t.me/+example", link.inviteLink)
        assertTrue(link.isRevoked)
        assertEquals("/revokeChatInviteLink", mockWebServer.takeRequest().path?.substringAfterLast("bot"))
    }

    private fun enqueueInviteLinkResponse(revoked: Boolean = false) {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """
                {
                    "ok": true,
                    "result": {
                        "invite_link": "https://t.me/+example",
                        "creator": { "id": 321, "is_bot": true, "first_name": "Test" },
                        "creates_join_request": false,
                        "is_primary": false,
                        "is_revoked": $revoked
                    }
                }
                """.trimIndent(),
            ),
        )
    }

    private companion object {
        const val CHAT_ID = -1001234567890L
    }
}
