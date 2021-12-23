package com.github.kotlintelegrambot.network

import okhttp3.ResponseBody

@Suppress("UNCHECKED_CAST")
internal suspend inline fun <T> call(crossinline block: suspend () -> CallResponse<T>): Pair<CallResponse<T?>?, Exception?> =
    try {
        Pair(block() as CallResponse<T?>?, null)
    } catch (e: Exception) {
        Pair(null, e)
    }

public class ResponseError(
    public val errorBody: ResponseBody?,
    public val exception: Exception?
)

public fun <T> Pair<CallResponse<T?>?, Exception?>.fold(
    response: (T?) -> Unit = {},
    error: (ResponseError) -> Unit = {}
) {
    if (first?.isSuccessful == true && first?.body() != null) response(first!!.body()!!)
    else error(ResponseError(first?.errorBody(), second))
}

public fun <T, R> Pair<CallResponse<T?>?, Exception?>.bimap(
    mapResponse: (T?) -> R,
    mapError: (ResponseError) -> R
): R =
    if (first?.isSuccessful == true && first?.body() != null) {
        val response = first!!.body()!!
        mapResponse(response)
    } else {
        mapError(ResponseError(first?.errorBody(), second))
    }
