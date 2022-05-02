package com.github.kotlintelegrambot.network.serialization.adapter

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.keyboard.WebAppInfo
import com.google.gson.GsonBuilder
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test

class InlineKeyboardButtonAdapterTest {

    private val sut = GsonBuilder().registerTypeAdapter(
        InlineKeyboardButton::class.java,
        InlineKeyboardButtonAdapter()
    ).create()

    @Test
    fun `serialize and deserialize inline keyboard url button`() {
        val urlButton = InlineKeyboardButton.Url(
            text = ANY_TEXT,
            url = ANY_URL
        )
        val urlButtonJson = """{"text":"$ANY_TEXT","url":"$ANY_URL"}"""

        val actualUrlButton = sut.fromJson(urlButtonJson, InlineKeyboardButton::class.java)
        val actualUrlButtonJson = sut.toJson(urlButton)

        assertEquals(urlButton, actualUrlButton)
        assertEquals(urlButtonJson, actualUrlButtonJson)
    }

    @Test
    fun `serialize and deserialize inline keyboard callback data button`() {
        val callbackDataButton = InlineKeyboardButton.CallbackData(
            text = ANY_TEXT,
            callbackData = ANY_CALLBACK_DATA
        )
        val callbackDataButtonJson = """{"text":"$ANY_TEXT","callback_data":"$ANY_CALLBACK_DATA"}"""

        val actualCallbackDataButton = sut.fromJson(
            callbackDataButtonJson,
            InlineKeyboardButton::class.java
        )
        val actualCallbackDataButtonJson = sut.toJson(callbackDataButton)

        assertEquals(callbackDataButton, actualCallbackDataButton)
        assertEquals(callbackDataButtonJson, actualCallbackDataButtonJson)
    }

    @Test
    fun `serialize and deserialize inline keyboard switch inline query button`() {
        val switchInlineQueryButton = InlineKeyboardButton.SwitchInlineQuery(
            text = ANY_TEXT,
            switchInlineQuery = ANY_SWITCH_INLINE_QUERY
        )
        val switchInlineQueryButtonJson =
            """{"text":"$ANY_TEXT","switch_inline_query":"$ANY_SWITCH_INLINE_QUERY"}"""

        val actualSwitchInlineQueryButton = sut.fromJson(
            switchInlineQueryButtonJson,
            InlineKeyboardButton::class.java
        )
        val actualSwitchInlineQueryButtonJson = sut.toJson(switchInlineQueryButton)

        assertEquals(switchInlineQueryButton, actualSwitchInlineQueryButton)
        assertEquals(switchInlineQueryButtonJson, actualSwitchInlineQueryButtonJson)
    }

    @Test
    fun `serialize and deserialize inline keyboard switch inline query current chat button`() {
        val switchInlineQueryCurrentChatButton = InlineKeyboardButton.SwitchInlineQueryCurrentChat(
            text = ANY_TEXT,
            switchInlineQueryCurrentChat = ANY_SWITCH_INLINE_QUERY
        )
        val switchInlineQueryCurrentChatButtonJson =
            """{"text":"$ANY_TEXT","switch_inline_query_current_chat":"$ANY_SWITCH_INLINE_QUERY"}"""

        val actualSwitchInlineQueryCurrentChatButton = sut.fromJson(
            switchInlineQueryCurrentChatButtonJson,
            InlineKeyboardButton::class.java
        )
        val actualSwitchInlineQueryCurrentChatJsonButton = sut.toJson(
            switchInlineQueryCurrentChatButton
        )

        assertEquals(switchInlineQueryCurrentChatButton, actualSwitchInlineQueryCurrentChatButton)
        assertEquals(switchInlineQueryCurrentChatButtonJson, actualSwitchInlineQueryCurrentChatJsonButton)
    }

    @Test
    fun `serialize and deserialize inline keyboard pay button`() {
        val payButton = InlineKeyboardButton.Pay(ANY_TEXT)
        val payButtonJson = """{"text":"$ANY_TEXT","pay":true}"""

        val actualPayButton = sut.fromJson(payButtonJson, InlineKeyboardButton::class.java)
        val actualPayButtonJson = sut.toJson(actualPayButton)

        assertEquals(payButton, actualPayButton)
        assertEquals(payButtonJson, actualPayButtonJson)
    }

    @Test
    fun `serialize and deserialize inline keyboard web app button`() {
        val webAppButton = InlineKeyboardButton.WebApp(
            text = ANY_TEXT,
            webApp = WebAppInfo(ANY_URL)
        )
        val webAppButtonJson =
            """{"text":"$ANY_TEXT","web_app":{"url":"$ANY_URL"}}"""

        val actualWebAppButton = sut.fromJson(
            webAppButtonJson,
            InlineKeyboardButton::class.java
        )
        val actualWebAppButtonJson = sut.toJson(
            webAppButton
        )

        assertEquals(webAppButton, actualWebAppButton)
        assertEquals(webAppButtonJson, actualWebAppButtonJson)
    }

    private companion object {
        const val ANY_TEXT = "Button :P"
        const val ANY_URL = "https://www.github.com/vjgarciag96"
        const val ANY_CALLBACK_DATA = "callback_data"
        const val ANY_SWITCH_INLINE_QUERY = "switch inline query"
    }
}
