package com.github.kotlintelegrambot.network

import com.google.gson.annotations.SerializedName as Name

data class Response<T>(
    val result: T?,
    val ok: Boolean,
    @Name("error_code") val errorCode: Int? = null,
    @Name("description") val errorDescription: String? = null
)
