package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class CallbackQuery(
    val id: String,
    val from: User,
    val message: Message?,
    @Name("inline_message_id") val inlineMessageId: String?,
    val data: String
)