package com.github.kotlintelegrambot.entities.checklists

import com.github.kotlintelegrambot.entities.MessageEntity
import com.google.gson.annotations.SerializedName

/**
 * Describes a checklist. (Bot API 9.1)
 *
 * See https://core.telegram.org/bots/api#checklist
 */
data class Checklist(
    @SerializedName("title") val title: String,
    @SerializedName("title_entities") val titleEntities: List<MessageEntity>? = null,
    @SerializedName("tasks") val tasks: List<ChecklistTask>,
    @SerializedName("others_can_add_tasks") val othersCanAddTasks: Boolean? = null,
    @SerializedName("others_can_mark_tasks_as_done") val othersCanMarkTasksAsDone: Boolean? = null,
)

/**
 * Describes a task in a checklist. (Bot API 9.1, extended in 9.3)
 *
 * See https://core.telegram.org/bots/api#checklisttask
 */
data class ChecklistTask(
    @SerializedName("id") val id: Int,
    @SerializedName("text") val text: String,
    @SerializedName("text_entities") val textEntities: List<MessageEntity>? = null,
    @SerializedName("completed_by_user") val completedByUser: com.github.kotlintelegrambot.entities.User? = null,
    @SerializedName("completion_date") val completionDate: Long? = null,
    @SerializedName("completed_by_chat") val completedByChat: com.github.kotlintelegrambot.entities.Chat? = null,
)
