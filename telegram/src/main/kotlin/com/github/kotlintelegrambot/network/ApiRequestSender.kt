package com.github.kotlintelegrambot.network

import retrofit2.Call

internal class ApiRequestSender {

    fun <T> send(apiRequestCall: Call<Response<T>>): CallResponse<Response<T>> = apiRequestCall.execute()
}
