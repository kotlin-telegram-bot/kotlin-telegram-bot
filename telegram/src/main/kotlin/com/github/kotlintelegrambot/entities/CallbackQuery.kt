package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class CallbackQuery(
    val id: String,
    val from: User,
    val message: Message? = null,
    @Name("inline_message_id") val inlineMessageId: String? = null,
    val data: String
)
