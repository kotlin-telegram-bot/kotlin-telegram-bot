package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.checklists.InputChecklist
import com.github.kotlintelegrambot.entities.checklists.InputChecklistTask
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for Bot API 9.1 checklist methods.
 */
class ChecklistsIT : ApiClientIT() {

    @Test
    fun `sendChecklist posts business_connection_id, chat_id and serialized checklist`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"ok":true,"result":{"message_id":1,"date":1700000000,"chat":{"id":1,"type":"private"}}}""",
            ),
        )

        sut.sendChecklist(
            businessConnectionId = "conn-abc",
            chatId = ChatId.fromId(11L),
            checklist = InputChecklist(
                title = "Groceries",
                tasks = listOf(InputChecklistTask(id = 1, text = "Eggs")),
            ),
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/sendChecklist"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("\"title\":\"Groceries\""))
        assertEquals(true, body.contains("\"text\":\"Eggs\""))
    }

    @Test
    fun `editMessageChecklist posts business_connection_id, chat_id, message_id and serialized checklist`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"ok":true,"result":{"message_id":1,"date":1700000000,"chat":{"id":1,"type":"private"}}}""",
            ),
        )

        sut.editMessageChecklist(
            businessConnectionId = "conn-abc",
            chatId = ChatId.fromId(11L),
            messageId = 7L,
            checklist = InputChecklist(
                title = "T",
                tasks = listOf(InputChecklistTask(id = 1, text = "x")),
            ),
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/editMessageChecklist"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("chat_id=11"))
        assertEquals(true, body.contains("message_id=7"))
        assertEquals(true, body.contains("\"title\":\"T\""))
        assertEquals(true, body.contains("\"text\":\"x\""))
    }
}
