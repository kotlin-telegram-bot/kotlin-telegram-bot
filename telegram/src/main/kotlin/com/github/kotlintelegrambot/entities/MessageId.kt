package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

data class MessageId(
    @SerializedName("message_id") val messageId: Long
)
