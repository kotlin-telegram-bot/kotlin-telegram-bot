package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.types.TelegramBotResult
import com.google.gson.Gson

internal class ApiResponseMapper {
    fun <T : Any> mapToTelegramBotResult(apiResponse: CallResponse<Response<T>>): TelegramBotResult<T> {
        fun invalidResponse(): TelegramBotResult.Error =
            TelegramBotResult.Error.InvalidResponse(
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

        // Only the ok / error_code / description fields are needed for the error path,
        // so we deserialize the raw wrapper without a generic capture. Gson 2.14+ rejects
        // TypeToken<Response<T>> when T is a method type variable, which is why we use the
        // raw class here and cast.
        @Suppress("UNCHECKED_CAST")
        val responseBody =
            try {
                Gson().fromJson(responseBodyString, Response::class.java) as Response<T>
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
