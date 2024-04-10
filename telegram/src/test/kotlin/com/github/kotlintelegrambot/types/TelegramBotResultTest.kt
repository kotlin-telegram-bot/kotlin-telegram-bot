package com.github.kotlintelegrambot.types

import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Exception
import java.lang.IllegalStateException

class TelegramBotResultTest {

    private val successSideEfect = mockk<(Int) -> Unit>(relaxed = true)
    private val errorSideEfect = mockk<(TelegramBotResult.Error) -> Unit>(relaxed = true)

    @Test
    fun `getOrNull with a success`() {
        val anyValue = 5
        val success = TelegramBotResult.Success(anyValue)

        assertEquals(anyValue, success.getOrNull())
    }

    @Test
    fun `getOrNull with an error`() {
        val error = TelegramBotResult.Error.Unknown(Exception())

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
        val error = TelegramBotResult.Error.Unknown(Exception())

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
        val error = TelegramBotResult.Error.Unknown(Exception())

        assertEquals(default, error.getOrDefault(default))
    }

    @Test
    fun `is the result a success`() {
        val success = TelegramBotResult.Success(1)
        assertTrue(success.isSuccess)

        val error = TelegramBotResult.Error.Unknown(Exception())
        assertFalse(error.isSuccess)
    }

    @Test
    fun `is the result an error`() {
        val success = TelegramBotResult.Success(1)
        assertFalse(success.isError)

        val error = TelegramBotResult.Error.Unknown(Exception())
        assertTrue(error.isError)
    }

    @Test
    fun `fold if success`() {
        val anyValue = 1
        val success = TelegramBotResult.Success(anyValue)

        val fSuccess: (Int) -> Int = { it + 4 }
        val foldResult = success.fold(
            ifSuccess = fSuccess,
            ifError = { },
        )

        assertEquals(fSuccess(anyValue), foldResult)
    }

    @Test
    fun `fold if error`() {
        val error = TelegramBotResult.Error.HttpError(400, "WTF")

        val fError: (error: TelegramBotResult.Error) -> Pair<Int, String?>? = {
            if (it is TelegramBotResult.Error.HttpError) {
                it.httpCode to it.description
            } else {
                null
            }
        }
        val foldResult = error.fold(
            ifSuccess = { },
            ifError = fError,
        )

        assertEquals(fError(error), foldResult)
    }

    @Test
    fun `onSuccess should be invoked when it is a success TelegramBotResult`() {
        val anyValue = 1
        val success = TelegramBotResult.Success(anyValue)

        success.onSuccess(successSideEfect)

        verify(exactly = 1) { successSideEfect.invoke(anyValue) }
    }

    @Test
    fun `onSuccess shouldn't be invoked when it is an error TelegramBotResult`() {
        val error = TelegramBotResult.Error.HttpError(400, "WTF")

        error.onSuccess(successSideEfect)

        verify(exactly = 0) { successSideEfect.invoke(any()) }
    }

    @Test
    fun `onError shouldn't be invoked when it is a success TelegramBotResult`() {
        val anyValue = 1
        val success = TelegramBotResult.Success(anyValue)

        success.onError(errorSideEfect)

        verify(exactly = 0) { errorSideEfect.invoke(any()) }
    }

    @Test
    fun `onError should be invoked when it is an error TelegramBotResult`() {
        val error = TelegramBotResult.Error.HttpError(400, "WTF")

        error.onError(errorSideEfect)

        verify(exactly = 1) { errorSideEfect.invoke(error) }
    }
}
