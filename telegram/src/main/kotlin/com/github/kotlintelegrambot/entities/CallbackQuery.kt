package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

data class CallbackQuery(
    val id: String,
    val from: User,
    val message: Message? = null,
    @SerializedName("inline_message_id") val inlineMessageId: String? = null,
    val data: String,
    @SerializedName("chat_instance") val chatInstance: String
)
