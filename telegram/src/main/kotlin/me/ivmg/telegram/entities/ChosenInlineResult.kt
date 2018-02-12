package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class ChosenInlineResult(
    @Name("result_id") val resultId: String,
    val from: User,
    val location: Location?,
    @Name("inline_message_id") val inlineMessageId: String?,
    val query: String
)