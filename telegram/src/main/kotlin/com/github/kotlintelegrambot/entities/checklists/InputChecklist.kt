package com.github.kotlintelegrambot.entities.checklists

import com.github.kotlintelegrambot.entities.MessageEntity
import com.github.kotlintelegrambot.entities.ParseMode
import com.google.gson.annotations.SerializedName

/**
 * Describes a checklist to create. (Bot API 9.1)
 *
 * See https://core.telegram.org/bots/api#inputchecklist
 */
data class InputChecklist(
    @SerializedName("title") val title: String,
    @SerializedName("parse_mode") val parseMode: ParseMode? = null,
    @SerializedName("title_entities") val titleEntities: List<MessageEntity>? = null,
    @SerializedName("tasks") val tasks: List<InputChecklistTask>,
    @SerializedName("others_can_add_tasks") val othersCanAddTasks: Boolean? = null,
    @SerializedName("others_can_mark_tasks_as_done") val othersCanMarkTasksAsDone: Boolean? = null,
)
