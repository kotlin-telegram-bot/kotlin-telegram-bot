package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatAdministratorRights
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.MenuButton
import com.github.kotlintelegrambot.entities.keyboard.WebAppInfo
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for the Bot API 6.6 / 6.7 bot-info methods.
 */
class BotInfoIT : ApiClientIT() {

    @Test
    fun `setMyDescription posts description and language_code`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setMyDescription(description = "A bot", languageCode = "en")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setMyDescription"))
        assertEquals(true, body.contains("description=A bot") || body.contains("description=A+bot"))
        assertEquals(true, body.contains("language_code=en"))
    }

    @Test
    fun `getMyDescription GETs with language_code`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":{"description":"desc"}}"""))

        sut.getMyDescription(languageCode = "en")

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(true, request.path?.contains("/getMyDescription"))
        assertEquals(true, request.path?.contains("language_code=en"))
    }

    @Test
    fun `setMyShortDescription posts short_description and language_code`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setMyShortDescription(shortDescription = "short", languageCode = "en")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setMyShortDescription"))
        assertEquals(true, body.contains("short_description=short"))
        assertEquals(true, body.contains("language_code=en"))
    }

    @Test
    fun `getMyShortDescription GETs with language_code`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":{"short_description":"sd"}}"""))

        sut.getMyShortDescription(languageCode = "en")

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(true, request.path?.contains("/getMyShortDescription"))
        assertEquals(true, request.path?.contains("language_code=en"))
    }

    @Test
    fun `setMyName posts name and language_code`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setMyName(name = "MyBot", languageCode = "en")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setMyName"))
        assertEquals(true, body.contains("name=MyBot"))
        assertEquals(true, body.contains("language_code=en"))
    }

    @Test
    fun `getMyName GETs with language_code`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":{"name":"n"}}"""))

        sut.getMyName(languageCode = "en")

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(true, request.path?.contains("/getMyName"))
        assertEquals(true, request.path?.contains("language_code=en"))
    }

    @Test
    fun `setChatMenuButton posts chat_id and serialized menu_button`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setChatMenuButton(
            chatId = ChatId.fromId(11L),
            menuButton = MenuButton.WebApp(text = "Open", webApp = WebAppInfo("https://example.com")),
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setChatMenuButton"))
        assertEquals(true, body.contains("chat_id=11"))
        assertEquals(true, body.contains("menu_button="))
        assertEquals(true, body.contains("\"type\":\"web_app\""))
        assertEquals(true, body.contains("\"text\":\"Open\""))
    }

    @Test
    fun `getChatMenuButton GETs with chat_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":{"type":"default"}}"""))

        sut.getChatMenuButton(chatId = ChatId.fromId(11L))

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(true, request.path?.contains("/getChatMenuButton"))
        assertEquals(true, request.path?.contains("chat_id=11"))
    }

    @Test
    fun `setMyDefaultAdministratorRights posts serialized rights and for_channels`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setMyDefaultAdministratorRights(
            rights = ChatAdministratorRights(
                isAnonymous = false,
                canManageChat = true,
                canDeleteMessages = true,
                canManageVideoChats = false,
                canRestrictMembers = true,
                canPromoteMembers = false,
                canChangeInfo = false,
                canInviteUsers = true,
            ),
            forChannels = false,
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setMyDefaultAdministratorRights"))
        assertEquals(true, body.contains("rights="))
        assertEquals(true, body.contains("\"can_manage_chat\":true"))
        assertEquals(true, body.contains("for_channels=false"))
    }

    @Test
    fun `getMyDefaultAdministratorRights GETs with for_channels`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"ok":true,"result":{"is_anonymous":false,"can_manage_chat":true,"can_delete_messages":true,"can_manage_video_chats":false,"can_restrict_members":true,"can_promote_members":false,"can_change_info":false,"can_invite_users":true}}""",
            ),
        )

        sut.getMyDefaultAdministratorRights(forChannels = true)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(true, request.path?.contains("/getMyDefaultAdministratorRights"))
        assertEquals(true, request.path?.contains("for_channels=true"))
    }
}
