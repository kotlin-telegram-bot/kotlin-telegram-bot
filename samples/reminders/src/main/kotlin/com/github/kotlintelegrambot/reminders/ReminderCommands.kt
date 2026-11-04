package com.github.kotlintelegrambot.reminders

import java.time.format.DateTimeFormatter

class ReminderCommands(
    private val service: ReminderService,
) {
    fun handle(
        chatId: Long,
        chatType: String,
        command: String,
        text: String = "",
    ): String {
        if (chatType != "private") return "Use this bot in a private chat."
        val reply =
            when (command) {
                "remind" -> service.draft(chatId, text)
                "confirm" -> service.confirm(chatId)
                "cancel" -> service.cancel(chatId)
                else -> return "Use /remind Call Alice tomorrow at 7pm, then /confirm or /cancel."
            }
        return when (reply.kind) {
            ReplyKind.PREVIEW -> {
                val reminder = checkNotNull(reply.reminder)
                "Draft: ${reminder.title}\nWhen: ${format(reminder)}\n" +
                    "Date-only phrases keep the current local clock time.\nUse /confirm to schedule or /cancel to discard."
            }
            ReplyKind.SCHEDULED -> "Scheduled: ${reply.reminder!!.title}\nWhen: ${format(reply.reminder)}"
            ReplyKind.NO_DRAFT -> "No draft. Use /remind Call Alice tomorrow at 7pm first."
            ReplyKind.EXPIRED -> "That draft is no longer in the future. Create a new /remind draft."
            ReplyKind.UNSUPPORTED -> "Use one time in the configured timezone. Ranges, recurrence, and message-specific timezones are not supported."
            ReplyKind.AMBIGUOUS -> "More than one time matched. Use one clear date and time."
            ReplyKind.NO_DATE -> "No clear time found. Try /remind Call Alice tomorrow at 19:00. Use AM/PM or a 24-hour clock."
            ReplyKind.NO_TITLE -> "Include a reminder title, for example /remind Call Alice tomorrow at 7pm."
            ReplyKind.PAST -> "The reminder must be in the future."
            ReplyKind.CAPACITY -> "This sample is full. Cancel an existing reminder or try again later."
            ReplyKind.ALREADY_SCHEDULED -> "This chat already has a scheduled reminder. /cancel clears it and the draft."
            ReplyKind.CANCELLED -> "Cancelled this chat's draft and scheduled reminder."
            ReplyKind.NOTHING_TO_CANCEL -> "This chat has no reminder to cancel."
            ReplyKind.UNAVAILABLE -> "The scheduler is unavailable. The reminder was not scheduled."
            ReplyKind.TOO_LONG -> "Use at most 2000 characters for a reminder."
        }
    }

    private fun format(reminder: Reminder): String = reminder.at.atZone(service.timeZone).format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss XXX '['VV']'"))
}
