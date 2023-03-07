package com.github.kotlintelegrambot.dispatcher.handlers

import anyCallbackQuery
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CallbackQueryHandlerTest {

    private val handleCallbackQueryMock = mockk<HandleCallbackQuery>(relaxed = true)

    @Test
    fun `checkUpdate returns false when there is no callback query`() {
        val anyUpdateWithoutCallbackQuery = anyUpdate(callbackQuery = null)
        val sut = CallbackQueryHandler(handleCallbackQuery = handleCallbackQueryMock)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithoutCallbackQuery)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is callback query and no callback data to match`() {
        val anyUpdateWithCallbackQuery = anyUpdate(callbackQuery = anyCallbackQuery())
        val sut = CallbackQueryHandler(
            callbackData = null,
            handleCallbackQuery = handleCallbackQueryMock
        )

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithCallbackQuery)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when there is callback query and its data doesn't match the data to match`() {
        val anyUpdateWithCallbackQuery = anyUpdate(
            callbackQuery = anyCallbackQuery(data = ANY_CALLBACK_QUERY_DATA)
        )
        val sut = CallbackQueryHandler(
            callbackData = ANY_OTHER_CALLBACK_QUERY_DATA,
            handleCallbackQuery = handleCallbackQueryMock
        )

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithCallbackQuery)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is callback query and its data is equal to the data to match`() {
        val anyUpdateWithCallbackQuery = anyUpdate(
            callbackQuery = anyCallbackQuery(data = ANY_CALLBACK_QUERY_DATA)
        )
        val sut = CallbackQueryHandler(
            callbackData = ANY_CALLBACK_QUERY_DATA,
            handleCallbackQuery = handleCallbackQueryMock
        )

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithCallbackQuery)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is callback query and its data contains the data to match`() {
        val anyUpdateWithCallbackQuery = anyUpdate(
            callbackQuery = anyCallbackQuery(data = ANY_CALLBACK_QUERY_DATA)
        )
        val sut = CallbackQueryHandler(
            callbackData = ANY_DATA_CONTAINED_IN_ANY_CALLBACK_QUERY_DATA,
            handleCallbackQuery = handleCallbackQueryMock
        )

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithCallbackQuery)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `callbackQuery is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>(relaxed = true)
        val anyCallbackQuery = anyCallbackQuery(data = ANY_CALLBACK_QUERY_DATA)
        val anyUpdateWithCallbackQuery = anyUpdate(callbackQuery = anyCallbackQuery)
        val sut = CallbackQueryHandler(
            callbackData = ANY_CALLBACK_QUERY_DATA,
            handleCallbackQuery = handleCallbackQueryMock
        )

        sut.handleUpdate(botMock, anyUpdateWithCallbackQuery)

        val expectedCallbackQueryHandlerEnvironment = CallbackQueryHandlerEnvironment(
            botMock,
            anyUpdateWithCallbackQuery,
            anyCallbackQuery
        )
        coVerify { handleCallbackQueryMock.invoke(expectedCallbackQueryHandlerEnvironment) }
    }

    @Test
    fun `callback query is answered when callbackQuery is dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>(relaxed = true)
        val anyUpdateWithCallbackQuery = anyUpdate(
            callbackQuery = anyCallbackQuery(
                id = ANY_CALLBACK_QUERY_ID,
                data = ANY_CALLBACK_QUERY_DATA
            )
        )
        val sut = CallbackQueryHandler(
            callbackData = ANY_CALLBACK_QUERY_DATA,
            handleCallbackQuery = handleCallbackQueryMock,
            callbackAnswerText = ANY_CALLBACK_ANSWER_TEXT,
            callbackAnswerUrl = ANY_ANSWER_CALLBACK_URL,
            callbackAnswerCacheTime = ANY_CALLBACK_ANSWER_CACHE_TIME,
            callbackAnswerShowAlert = CALLBACK_ANSWER_SHOW_ALERT
        )

        sut.handleUpdate(botMock, anyUpdateWithCallbackQuery)

        verify {
            botMock.answerCallbackQuery(
                ANY_CALLBACK_QUERY_ID,
                ANY_CALLBACK_ANSWER_TEXT,
                CALLBACK_ANSWER_SHOW_ALERT,
                ANY_ANSWER_CALLBACK_URL,
                ANY_CALLBACK_ANSWER_CACHE_TIME
            )
        }
    }

    private companion object {
        const val ANY_CALLBACK_QUERY_DATA = "yeheeee"
        const val ANY_OTHER_CALLBACK_QUERY_DATA = "yiiiiiihiii"
        const val ANY_DATA_CONTAINED_IN_ANY_CALLBACK_QUERY_DATA = "yeh"
        const val ANY_CALLBACK_QUERY_ID = "534241"
        const val ANY_CALLBACK_ANSWER_TEXT = "answer test"
        const val ANY_ANSWER_CALLBACK_URL = "http://www.telegram.com"
        const val ANY_CALLBACK_ANSWER_CACHE_TIME = 342
        const val CALLBACK_ANSWER_SHOW_ALERT = true
    }
}
