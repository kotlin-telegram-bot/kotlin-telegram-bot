package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

data class SentWebAppMessage(
    @SerializedName("inline_message_id") val inlineMessageId: String,
)
