package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.types.TelegramBotResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

internal class ApiResponseMapper {

    fun <T : Any> mapToTelegramBotResult(apiResponse: CallResponse<Response<T>>): TelegramBotResult<T> {
        fun invalidResponse(): TelegramBotResult.Error = TelegramBotResult.Error.InvalidResponse(
            apiResponse.code(),
            apiResponse.message(),
            apiResponse.body(),
        )

        fun Response<T>.getTelegramErrorOrInvalidResponse(): TelegramBotResult.Error {
            return TelegramBotResult.Error.TelegramApi(
                errorCode = this.errorCode ?: return invalidResponse(),
                description = this.errorDescription ?: return invalidResponse(),
            )
        }

        if (apiResponse.isSuccessful) {
            val responseBody = apiResponse.body() ?: return invalidResponse()
            return if (responseBody.ok) {
                val telegramResult = responseBody.result ?: return invalidResponse()

                TelegramBotResult.Success(telegramResult)
            } else {
                responseBody.getTelegramErrorOrInvalidResponse()
            }
        }

        val responseBodyString = apiResponse.errorBody()?.string() ?: return invalidResponse()

        val responseBody = try {
            val type = object : TypeToken<Response<T>>() {}.type
            Gson().fromJson<Response<T>>(responseBodyString, type)
        } catch (e: Exception) {
            return TelegramBotResult.Error.HttpError(
                apiResponse.code(),
                responseBodyString,
            )
        }

        if (responseBody.ok) return invalidResponse()

        return responseBody.getTelegramErrorOrInvalidResponse()
    }
}
