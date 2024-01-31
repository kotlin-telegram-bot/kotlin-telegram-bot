package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.WebAppInfo
import com.google.gson.annotations.SerializedName

data class InlineQueryResultsButton(
    val text: String,
    @SerializedName("start_parameter") var startParameter: String? = null,
    @SerializedName("web_app") var webApp: WebAppInfo? = null,
)
