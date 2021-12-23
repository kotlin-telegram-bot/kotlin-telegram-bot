package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

public data class ResponseParameters(
    @Name("migrate_to_chat_id") val migrateToChatId: Long? = null,
    @Name("retry_after") val replyAfter: Long? = null
)
