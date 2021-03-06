package com.github.kotlintelegrambot.entities

import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class InlineKeyboardMarkupTest {

    @Test
    fun `can't create an inline keyboard with two pay buttons`() {
        assertThrows<IllegalArgumentException> {
            InlineKeyboardMarkup.create(
                listOf(anyInlineKeyboardButtonPay()),
                listOf(anyInlineKeyboardButtonPay())
            )
        }
    }

    @Test
    fun `can't create an inline keyboard with a pay button not first in first row`() {
        assertThrows<IllegalArgumentException> {
            InlineKeyboardMarkup.createSingleRowKeyboard(
                anyInlineKeyboardButtonUrl(),
                anyInlineKeyboardButtonPay()
            )
        }
    }

    @Test
    fun `can't create an inline keyboard with two CallbackGameButtonType buttons`() {
        assertThrows<IllegalArgumentException> {
            InlineKeyboardMarkup.create(
                listOf(anyInlineKeyboardButtonCallbackGameButtonType()),
                listOf(anyInlineKeyboardButtonCallbackGameButtonType())
            )
        }
    }

    @Test
    fun `can't create an inline keyboard with a CallbackGameButtonType button not first in first row`() {
        assertThrows<IllegalArgumentException> {
            InlineKeyboardMarkup.createSingleRowKeyboard(
                anyInlineKeyboardButtonUrl(),
                anyInlineKeyboardButtonCallbackGameButtonType()
            )
        }
    }

    @Test
    fun `create an inline keyboard with pay button first in first row`() {
        val payButton = anyInlineKeyboardButtonPay()
        val urlButton = anyInlineKeyboardButtonUrl()

        val inlineKeyboard = InlineKeyboardMarkup.createSingleRowKeyboard(payButton, urlButton)

        assertEquals(payButton, inlineKeyboard.inlineKeyboard.flatten().first())
        assertEquals(urlButton, inlineKeyboard.inlineKeyboard.flatten().last())
    }

    @Test
    fun `create an inline keyboard with CallbackGameButtonType button first in first row`() {
        val callbackGameButton = anyInlineKeyboardButtonCallbackGameButtonType()
        val urlButton = anyInlineKeyboardButtonUrl()

        val inlineKeyboard = InlineKeyboardMarkup.createSingleRowKeyboard(callbackGameButton, urlButton)

        assertEquals(callbackGameButton, inlineKeyboard.inlineKeyboard.flatten().first())
        assertEquals(urlButton, inlineKeyboard.inlineKeyboard.flatten().last())
    }

    @Test
    fun `create an inline keyboard with url buttons`() {
        val urlButton1 = anyInlineKeyboardButtonUrl()
        val urlButton2 = anyInlineKeyboardButtonUrl()

        val inlineKeyboard = InlineKeyboardMarkup.create(listOf(urlButton1), listOf(urlButton2))

        assertEquals(urlButton1, inlineKeyboard.inlineKeyboard.flatten().first())
        assertEquals(urlButton2, inlineKeyboard.inlineKeyboard.flatten().last())
    }
}
