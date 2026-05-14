package com.github.kotlintelegrambot.entities.checklists

import com.google.gson.annotations.SerializedName

/**
 * Describes a service message about checklist tasks marked as done or not done. (Bot API 9.1)
 *
 * See https://core.telegram.org/bots/api#checklisttasksdone
 */
data class ChecklistTasksDone(
    @SerializedName("checklist_message") val checklistMessage: com.github.kotlintelegrambot.entities.Message? = null,
    @SerializedName("marked_as_done_task_ids") val markedAsDoneTaskIds: List<Int>? = null,
    @SerializedName("marked_as_not_done_task_ids") val markedAsNotDoneTaskIds: List<Int>? = null,
)
