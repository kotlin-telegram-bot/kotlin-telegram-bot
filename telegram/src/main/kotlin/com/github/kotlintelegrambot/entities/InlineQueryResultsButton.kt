package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.keyboard.WebAppInfo
import com.google.gson.annotations.SerializedName as Name

/**
 * Represents a button to be shown above inline query results. You must use exactly one of the
 * optional fields.
 *
 * See https://core.telegram.org/bots/api#inlinequeryresultsbutton
 */
data class InlineQueryResultsButton(
    @Name("text") val text: String,
    @Name("web_app") val webApp: WebAppInfo? = null,
    @Name("start_parameter") val startParameter: String? = null,
)
