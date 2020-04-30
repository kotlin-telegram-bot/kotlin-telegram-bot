package com.github.kotlintelegrambot.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

fun <T> Call<T>.call(): Pair<Response<T?>?, Exception?> = try {
    Pair(execute(), null)
} catch (exception: Exception) {
    Pair(null, exception)
}

class ResponseError(val errorBody: ResponseBody?, val exception: Exception?)

fun <T> Pair<Response<T?>?, Exception?>.fold(response: (T?) -> Unit = {}, error: (ResponseError) -> Unit = {}) {
    if (first?.isSuccessful == true && first?.body() != null) response(first!!.body()!!)
    else error(ResponseError(first?.errorBody(), second))
}

fun <T, R> Pair<Response<T?>?, Exception?>.bimap(mapResponse: (T?) -> R, mapError: (ResponseError) -> R): R =
    if (first?.isSuccessful == true && first?.body() != null) {
        val response = first!!.body()!!
        mapResponse(response)
    } else {
        mapError(ResponseError(first?.errorBody(), second))
    }
