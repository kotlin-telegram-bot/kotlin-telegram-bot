package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.types.TelegramBotResult
import junit.framework.TestCase.assertEquals
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test

class ApiResponseMapperTest {

    private val sut = ApiResponseMapper()

    @Test
    fun `error response`() {
        val notSuccessfulResponse = CallResponse.error<Response<Int>>(
            ANY_HTTP_ERROR_CODE,
            ANY_ERROR_BODY.toResponseBody()
        )

        val telegramBotResult = sut.mapToTelegramBotResult(notSuccessfulResponse)

        val expectedTelegramBotResult = TelegramBotResult.Error.HttpError<Int>(
            ANY_HTTP_ERROR_CODE,
            ANY_ERROR_BODY
        )
        assertEquals(expectedTelegramBotResult, telegramBotResult)
    }

    @Test
    fun `successful response with no body`() {
        val successfulResponseWithNoBody = CallResponse.success<Response<Int>>(null)

        val telegramBotResult = sut.mapToTelegramBotResult(successfulResponseWithNoBody)

        val expectedTelegramBotResult = TelegramBotResult.Error.InvalidResponse<Int>(
            200,
            "OK",
            null
        )
        assertEquals(expectedTelegramBotResult, telegramBotResult)
    }

    @Test
    fun `successful response with invalid successful Telegram response`() {
        val invalidSuccessfulTgResponse = Response<Int>(
            result = null,
            ok = true,
            errorCode = null,
            errorDescription = null
        )
        val successfulResponseWithInvalidSuccessfulTgResponse = CallResponse.success(
            invalidSuccessfulTgResponse
        )

        val telegramBotResult = sut.mapToTelegramBotResult(
            successfulResponseWithInvalidSuccessfulTgResponse
        )

        val expectedTelegramBotResult = TelegramBotResult.Error.InvalidResponse(
            200,
            "OK",
            invalidSuccessfulTgResponse
        )
        assertEquals(expectedTelegramBotResult, telegramBotResult)
    }

    @Test
    fun `successful response with valid successful Telegram response`() {
        val validSuccessfulTgResponse = Response(
            result = ANY_RESULT,
            ok = true,
            errorCode = null,
            errorDescription = null
        )
        val successfulResponseWithValidSuccessfulTgResponse = CallResponse.success(
            validSuccessfulTgResponse
        )

        val telegramBotResult = sut.mapToTelegramBotResult(
            successfulResponseWithValidSuccessfulTgResponse
        )

        val expectedTelegramBotResult = TelegramBotResult.Success(ANY_RESULT)
        assertEquals(expectedTelegramBotResult, telegramBotResult)
    }

    @Test
    fun `successful response with invalid error Telegram response in error code`() {
        val invalidErrorTgResponse = Response(
            result = null,
            ok = false,
            errorCode = null,
            errorDescription = "any error description"
        )
        val successfulResponseWithInvalidErrorTgResponse = CallResponse.success(
            invalidErrorTgResponse
        )

        val telegramBotResult = sut.mapToTelegramBotResult(
            successfulResponseWithInvalidErrorTgResponse
        )

        val expectedTelegramBotResult = TelegramBotResult.Error.InvalidResponse(
            200,
            "OK",
            invalidErrorTgResponse
        )
        assertEquals(expectedTelegramBotResult, telegramBotResult)
    }

    @Test
    fun `successful response with invalid error Telegram response in error description`() {
        val invalidErrorTgResponse = Response(
            result = null,
            ok = false,
            errorCode = 403,
            errorDescription = null
        )
        val successfulResponseWithInvalidErrorTgResponse = CallResponse.success(
            invalidErrorTgResponse
        )

        val telegramBotResult = sut.mapToTelegramBotResult(
            successfulResponseWithInvalidErrorTgResponse
        )

        val expectedTelegramBotResult = TelegramBotResult.Error.InvalidResponse(
            200,
            "OK",
            invalidErrorTgResponse
        )
        assertEquals(expectedTelegramBotResult, telegramBotResult)
    }

    @Test
    fun `successful response with valid error Telegram response`() {
        val validErrorTgResponse = Response(
            result = null,
            ok = false,
            errorCode = ANY_ERROR_CODE,
            errorDescription = ANY_ERROR_DESCRIPTION
        )
        val successfulResponseWithValidErrorTgResponse = CallResponse.success(
            validErrorTgResponse
        )

        val telegramBotResult = sut.mapToTelegramBotResult(
            successfulResponseWithValidErrorTgResponse
        )

        val expectedTelegramBotResult = TelegramBotResult.Error.TelegramApi<Int>(
            ANY_ERROR_CODE,
            ANY_ERROR_DESCRIPTION
        )
        assertEquals(expectedTelegramBotResult, telegramBotResult)
    }

    private companion object {
        const val ANY_HTTP_ERROR_CODE = 401
        const val ANY_ERROR_BODY = "Proletharian"
        const val ANY_RESULT = 5
        const val ANY_ERROR_CODE = 404
        const val ANY_ERROR_DESCRIPTION = "Hodor"
    }
}
