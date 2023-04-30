package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.types.TelegramBotResult
import com.google.gson.Gson

internal class ApiResponseMapper {
    fun <T> mapToTelegramBotResult(
        apiResponse: CallResponse<Response<T>>
    ): TelegramBotResult<T> = when {
        apiResponse.isSuccessful ->
            createSuccessResult(apiResponse)
        else ->
            createErrorResult(apiResponse)
    }

    private fun <T> createSuccessResult(apiResponse: CallResponse<Response<T>>): TelegramBotResult.Success<T> {
        return TelegramBotResult.Success(apiResponse.body()!!.result!!)
    }

    private fun <T> createErrorResult(apiResponse: CallResponse<Response<T>>): TelegramBotResult.Error.TelegramApi<T> {
        val responseBody = Gson().fromJson(apiResponse.errorBody()!!.charStream(), Response::class.java)
        return TelegramBotResult.Error.TelegramApi(
            errorCode = responseBody.errorCode!!,
            description = responseBody.errorDescription!!
        )
    }
}
