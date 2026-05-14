package com.github.kotlintelegrambot.entities.checklists

import com.google.gson.annotations.SerializedName

/**
 * Describes a service message about tasks added to a checklist. (Bot API 9.1)
 *
 * See https://core.telegram.org/bots/api#checklisttasksadded
 */
data class ChecklistTasksAdded(
    @SerializedName("checklist_message") val checklistMessage: com.github.kotlintelegrambot.entities.Message? = null,
    @SerializedName("tasks") val tasks: List<ChecklistTask>,
)
