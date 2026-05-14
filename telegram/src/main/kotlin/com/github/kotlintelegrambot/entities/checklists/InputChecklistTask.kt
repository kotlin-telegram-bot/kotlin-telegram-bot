package com.github.kotlintelegrambot.entities.checklists

import com.github.kotlintelegrambot.entities.MessageEntity
import com.github.kotlintelegrambot.entities.ParseMode
import com.google.gson.annotations.SerializedName

/**
 * Describes a task to add to a checklist. (Bot API 9.1)
 *
 * See https://core.telegram.org/bots/api#inputchecklisttask
 */
data class InputChecklistTask(
    @SerializedName("id") val id: Int,
    @SerializedName("text") val text: String,
    @SerializedName("parse_mode") val parseMode: ParseMode? = null,
    @SerializedName("text_entities") val textEntities: List<MessageEntity>? = null,
)
