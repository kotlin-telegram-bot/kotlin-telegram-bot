package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.types.TelegramBotResult

internal class ApiResponseMapper {

    fun <T> mapToTelegramBotResult(apiResponse: CallResponse<Response<T>>): TelegramBotResult<T> {
        fun invalidResponse() = TelegramBotResult.Error.InvalidResponse(
            apiResponse.code(),
            apiResponse.message(),
            apiResponse.body()
        )

        if (!apiResponse.isSuccessful) {
            return TelegramBotResult.Error.HttpError(
                apiResponse.code(),
                apiResponse.errorBody()?.string()
            )
        }

        val responseBody = apiResponse.body() ?: return invalidResponse()

        if (responseBody.ok) {
            val telegramResult = responseBody.result ?: return invalidResponse()

            return TelegramBotResult.Success(telegramResult)
        } else {
            val telegramErrorCode = responseBody.errorCode ?: return invalidResponse()
            val telegramErrorDescription = responseBody.errorDescription ?: return invalidResponse()

            return TelegramBotResult.Error.TelegramApi(
                errorCode = telegramErrorCode,
                description = telegramErrorDescription
            )
        }
    }
}
