package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import java.lang.IllegalArgumentException
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InlineKeyboardMarkupTest {

    @Test
    fun `can't create an inline keyboard with two pay buttons`() {
        assertThrows<IllegalArgumentException> {
            InlineKeyboardMarkup.create(
                listOf(InlineKeyboardButton.Pay("1")),
                listOf(InlineKeyboardButton.Pay("2"))
            )
        }
    }

    @Test
    fun `can't create an inline keyboard with a pay button not first in first row`() {
        assertThrows<IllegalArgumentException> {
            InlineKeyboardMarkup.createSingleRowKeyboard(
                InlineKeyboardButton.Url("1", "https://www.github.com"),
                InlineKeyboardButton.Pay("2")
            )
        }
    }

    @Test
    fun `create an inline keyboard with pay button first in first row`() {
        val payButton = InlineKeyboardButton.Pay("1")
        val urlButton = InlineKeyboardButton.Url("3", "http://noone.com")

        val inlineKeyboard = InlineKeyboardMarkup.createSingleRowKeyboard(payButton, urlButton)

        assertEquals(payButton, inlineKeyboard.inlineKeyboard.flatten().first())
        assertEquals(urlButton, inlineKeyboard.inlineKeyboard.flatten().last())
    }

    @Test
    fun `create an inline keyboard with url buttons`() {
        val urlButton1 = InlineKeyboardButton.Url("3", "http://noone.com")
        val urlButton2 = InlineKeyboardButton.Url("5", "http://noone.com/2")

        val inlineKeyboard = InlineKeyboardMarkup.create(listOf(urlButton1), listOf(urlButton2))

        assertEquals(urlButton1, inlineKeyboard.inlineKeyboard.flatten().first())
        assertEquals(urlButton2, inlineKeyboard.inlineKeyboard.flatten().last())
    }
}
