package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.apiMethodName
import com.github.kotlintelegrambot.testutils.decodedBody
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class PromoteChatMemberIT : ApiClientIT() {

    @Test
    fun `correct request with all mandatory arguments`() {
        givenPromoteChatMemberSuccessResponse()

        sut.promoteChatMember(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            userId = ANY_USER_ID,
            isAnonymous = null,
            canChangeInfo = null,
            canPostMessages = null,
            canEditMessages = null,
            canDeleteMessages = null,
            canInviteUsers = null,
            canRestrictMembers = null,
            canPinMessages = null,
            canPromoteMembers = null,
        )

        val request = mockWebServer.takeRequest()
        assertEquals("promoteChatMember", request.apiMethodName)
        assertEquals(
            "chat_id=$ANY_CHAT_ID&user_id=$ANY_USER_ID",
            request.decodedBody
        )
    }

    @Test
    fun `correct request with all arguments`() {
        givenPromoteChatMemberSuccessResponse()

        sut.promoteChatMember(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            userId = ANY_USER_ID,
            isAnonymous = IS_ANONYMOUS,
            canChangeInfo = CAN_CHANGE_INFO,
            canPostMessages = CAN_POST_MESSAGES,
            canEditMessages = CAN_EDIT_MESSAGES,
            canDeleteMessages = CAN_DELETE_MESSAGES,
            canInviteUsers = CAN_INVITE_USERS,
            canRestrictMembers = CAN_RESTRICT_MEMBERS,
            canPinMessages = CAN_PIN_MESSAGES,
            canPromoteMembers = CAN_PROMOTE_MEMBERS,
        )

        val request = mockWebServer.takeRequest()
        assertEquals("promoteChatMember", request.apiMethodName)
        assertEquals(
            "chat_id=$ANY_CHAT_ID&user_id=$ANY_USER_ID&is_anonymous=$IS_ANONYMOUS" +
                "&can_change_info=$CAN_CHANGE_INFO&can_post_messages=$CAN_POST_MESSAGES" +
                "&can_edit_messages=$CAN_EDIT_MESSAGES&can_delete_messages=$CAN_DELETE_MESSAGES" +
                "&can_invite_users=$CAN_INVITE_USERS&can_restrict_members=$CAN_RESTRICT_MEMBERS" +
                "&can_pin_messages=$CAN_PIN_MESSAGES&can_promote_members=$CAN_PROMOTE_MEMBERS",
            request.decodedBody
        )
    }

    @Test
    fun `successful response is returned correctly`() {
        givenPromoteChatMemberSuccessResponse()

        val promoteChatMemberResult = sut.promoteChatMember(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            userId = ANY_USER_ID,
            isAnonymous = null,
            canChangeInfo = null,
            canPostMessages = null,
            canEditMessages = null,
            canDeleteMessages = null,
            canInviteUsers = null,
            canRestrictMembers = null,
            canPinMessages = null,
            canPromoteMembers = null,
        )

        assertEquals(true, promoteChatMemberResult.getOrNull())
    }

    private fun givenPromoteChatMemberSuccessResponse() {
        val promoteChatMemberResponseBody = """
            {
                "ok": true,
                "result": true 
            }
        """.trimIndent()
        val mockedSuccessResponse = MockResponse()
            .setResponseCode(200)
            .setBody(promoteChatMemberResponseBody)
        mockWebServer.enqueue(mockedSuccessResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 35123523L
        const val ANY_USER_ID = 12124124L
        const val IS_ANONYMOUS = true
        const val CAN_CHANGE_INFO = true
        const val CAN_POST_MESSAGES = true
        const val CAN_EDIT_MESSAGES = true
        const val CAN_DELETE_MESSAGES = true
        const val CAN_INVITE_USERS = true
        const val CAN_RESTRICT_MEMBERS = true
        const val CAN_PIN_MESSAGES = true
        const val CAN_PROMOTE_MEMBERS = true
    }
}
