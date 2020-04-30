package com.github.kotlintelegrambot

import com.github.kotlintelegrambot.entities.PollOption
import com.google.gson.annotations.SerializedName as Name

data class Poll(
    val id: Long,
    val question: String,
    val options: List<PollOption>,
    @Name("is_closed") val isClosed: Boolean
)
