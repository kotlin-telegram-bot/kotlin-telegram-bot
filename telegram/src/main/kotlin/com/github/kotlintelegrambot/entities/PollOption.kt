package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class PollOption(
    val text: String,
    @Name("voter_count") val voterCount: Int
)
