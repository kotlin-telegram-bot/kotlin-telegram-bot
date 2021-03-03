package com.github.kotlintelegrambot.types

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Exception
import java.lang.IllegalStateException

class TelegramBotResultTest {

    @Test
    fun `getOrNull with a success`() {
        val anyValue = 5
        val success = TelegramBotResult.Success(anyValue)

        assertEquals(anyValue, success.getOrNull())
    }

    @Test
    fun `getOrNull with an error`() {
        val error = TelegramBotResult.Error.Unknown<Int>(Exception())

        assertNull(error.getOrNull())
    }

    @Test
    fun `get with a success`() {
        val anyValue = 3
        val success = TelegramBotResult.Success(anyValue)

        assertEquals(anyValue, success.get())
    }

    @Test
    fun `get with an error`() {
        val error = TelegramBotResult.Error.Unknown<Int>(Exception())

        assertThrows<IllegalStateException> {
            error.get()
        }
    }

    @Test
    fun `getOrDefault with a success`() {
        val anyValue = 77
        val default = 55
        val success = TelegramBotResult.Success(anyValue)

        assertEquals(anyValue, success.getOrDefault(default))
    }

    @Test
    fun `getOrDefault with an error`() {
        val default = 55
        val error = TelegramBotResult.Error.Unknown<Int>(Exception())

        assertEquals(default, error.getOrDefault(default))
    }

    @Test
    fun `is the result a success`() {
        val success = TelegramBotResult.Success(1)
        assertTrue(success.isSuccess)

        val error = TelegramBotResult.Error.Unknown<Int>(Exception())
        assertFalse(error.isSuccess)
    }

    @Test
    fun `is the result an error`() {
        val success = TelegramBotResult.Success(1)
        assertFalse(success.isError)

        val error = TelegramBotResult.Error.Unknown<Int>(Exception())
        assertTrue(error.isError)
    }

    @Test
    fun `fold if success`() {
        val anyValue = 1
        val success = TelegramBotResult.Success(anyValue)

        val fSuccess: (Int) -> Int = { it + 4 }
        val foldResult = success.fold(
            ifSuccess = fSuccess,
            ifError = { }
        )

        assertEquals(fSuccess(anyValue), foldResult)
    }

    @Test
    fun `fold if error`() {
        val error = TelegramBotResult.Error.HttpError<Int>(400, "WTF")

        val fError: (error: TelegramBotResult.Error<Int>) -> Pair<Int, String?>? = {
            if (it is TelegramBotResult.Error.HttpError) {
                it.httpCode to it.description
            } else {
                null
            }
        }
        val foldResult = error.fold(
            ifSuccess = { },
            ifError = fError
        )

        assertEquals(fError(error), foldResult)
    }
}
