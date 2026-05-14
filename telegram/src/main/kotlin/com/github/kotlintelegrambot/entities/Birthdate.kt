package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

/**
 * Describes the birthdate of a user. (Bot API 7.3)
 *
 * https://core.telegram.org/bots/api#birthdate
 */
data class Birthdate(
    @Name("day") val day: Int,
    @Name("month") val month: Int,
    @Name("year") val year: Int? = null,
)
