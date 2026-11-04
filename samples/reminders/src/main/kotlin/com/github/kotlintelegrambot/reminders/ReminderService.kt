package com.github.kotlintelegrambot.reminders

import io.github.alox1d.dateparser.DateParser
import kotlinx.datetime.TimeZone
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

data class Reminder(
    val title: String,
    val at: Instant,
)

enum class ReplyKind {
    PREVIEW,
    SCHEDULED,
    NO_DRAFT,
    EXPIRED,
    UNSUPPORTED,
    AMBIGUOUS,
    NO_DATE,
    NO_TITLE,
    PAST,
    CAPACITY,
    ALREADY_SCHEDULED,
    CANCELLED,
    NOTHING_TO_CANCEL,
    UNAVAILABLE,
    TOO_LONG,
}

data class ReminderReply(
    val kind: ReplyKind,
    val reminder: Reminder? = null,
)

fun interface Cancellation {
    fun cancel()
}

fun interface ReminderScheduler {
    // Register delayed work without waiting for the due time or executing network I/O here.
    fun schedule(
        at: Instant,
        action: () -> Unit,
    ): Cancellation
}

class ReminderService(
    private val clock: Clock,
    val timeZone: ZoneId,
    private val scheduler: ReminderScheduler,
    private val capacity: Int = 100,
    private val deliver: (Long, Reminder) -> Unit,
) : AutoCloseable {
    private class Scheduled(
        val reminder: Reminder,
        var cancellation: Cancellation? = null,
    )

    private val lock = Any()
    private val drafts = mutableMapOf<Long, Reminder>()
    private val scheduled = mutableMapOf<Long, Scheduled>()
    private val parser = DateParser()
    private val parserTimeZone = TimeZone.of(timeZone.id)
    private var closed = false

    init {
        require(capacity > 0)
    }

    fun draft(
        chatId: Long,
        text: String,
    ): ReminderReply =
        synchronized(lock) {
            if (closed) return@synchronized ReminderReply(ReplyKind.UNAVAILABLE)
            // An invalid replacement must not leave an earlier preview confirmable.
            drafts.remove(chatId)
            val now = clock.instant()
            drafts.entries.removeIf { !it.value.at.isAfter(now) }
            if (text.length > 2000) return@synchronized ReminderReply(ReplyKind.TOO_LONG)
            if (invalidClock(text)) return@synchronized ReminderReply(ReplyKind.NO_DATE)
            if (unsupportedSyntax.containsMatchIn(text) || embeddedZone.containsMatchIn(text)) {
                return@synchronized ReminderReply(ReplyKind.UNSUPPORTED)
            }

            val matches =
                parser.parse(
                    text,
                    reference = kotlin.time.Instant.parse(now.toString()),
                    timeZone = parserTimeZone,
                )
            if (matches.isEmpty()) return@synchronized ReminderReply(ReplyKind.NO_DATE)
            if (matches.any { it.recurrence != null || it.endInstant != null }) {
                return@synchronized ReminderReply(ReplyKind.UNSUPPORTED)
            }
            if (matches.size != 1) return@synchronized ReminderReply(ReplyKind.AMBIGUOUS)
            val match = matches.single()
            val title = match.remainingText.trim()
            if (title.isBlank()) return@synchronized ReminderReply(ReplyKind.NO_TITLE)
            // Do not silently treat an unconsumed clock/date fragment as part of a title.
            if (unresolvedTime.containsMatchIn(title)) return@synchronized ReminderReply(ReplyKind.NO_DATE)
            val reminder = Reminder(title, Instant.parse(match.instant.toString()))
            if (!reminder.at.isAfter(now)) return@synchronized ReminderReply(ReplyKind.PAST)
            if (drafts.size + scheduled.size >= capacity) return@synchronized ReminderReply(ReplyKind.CAPACITY)
            drafts[chatId] = reminder
            ReminderReply(ReplyKind.PREVIEW, reminder)
        }

    fun confirm(chatId: Long): ReminderReply =
        synchronized(lock) {
            if (closed) return@synchronized ReminderReply(ReplyKind.UNAVAILABLE)
            val reminder = drafts[chatId] ?: return@synchronized ReminderReply(ReplyKind.NO_DRAFT)
            if (!reminder.at.isAfter(clock.instant())) {
                drafts.remove(chatId)
                return@synchronized ReminderReply(ReplyKind.EXPIRED)
            }
            if (chatId in scheduled) return@synchronized ReminderReply(ReplyKind.ALREADY_SCHEDULED)
            val pending = Scheduled(reminder)
            scheduled[chatId] = pending
            try {
                pending.cancellation = scheduler.schedule(reminder.at) { fire(chatId, pending) }
            } catch (_: RuntimeException) {
                scheduled.remove(chatId)
                return@synchronized ReminderReply(ReplyKind.UNAVAILABLE)
            }
            drafts.remove(chatId)
            ReminderReply(ReplyKind.SCHEDULED, reminder)
        }

    fun cancel(chatId: Long): ReminderReply =
        synchronized(lock) {
            val draft = drafts.remove(chatId)
            val pending = scheduled.remove(chatId)
            pending?.cancellation?.cancel()
            ReminderReply(if (draft != null || pending != null) ReplyKind.CANCELLED else ReplyKind.NOTHING_TO_CANCEL)
        }

    private fun fire(
        chatId: Long,
        pending: Scheduled,
    ) {
        val reminder =
            synchronized(lock) {
                // Identity prevents a cancelled callback from consuming a newer reminder in this chat.
                if (closed || scheduled[chatId] !== pending) return
                scheduled.remove(chatId)
                pending.reminder
            }
        // Claim the delivery before network I/O; duplicate callbacks cannot send twice.
        deliver(chatId, reminder)
    }

    override fun close() {
        synchronized(lock) {
            closed = true
            drafts.clear()
            scheduled.values.forEach { it.cancellation?.cancel() }
            scheduled.clear()
        }
    }

    private fun invalidClock(text: String): Boolean =
        clocks.findAll(text).any { match ->
            val hour = match.groupValues[1].toIntOrNull()
            val minute = match.groupValues[2].toIntOrNull()
            val second = match.groupValues[3].ifEmpty { "0" }.toIntOrNull()
            hour == null || hour !in 0..23 || minute == null || minute !in 0..59 || second == null || second !in 0..59
        }

    private companion object {
        val clocks = Regex("(?<![\\p{L}\\d])(\\d+):(\\d+)(?::(\\d+))?(?!\\d)")
        val unresolvedTime = Regex("\\d\\s*[:/]|(?i)(?<![\\p{L}])(?:at|в)\\s+\\d|\\d\\s*[ap]\\.?m\\.?(?![\\p{L}])")

        // Some common unsupported phrases produce point matches in DateParser 0.1.3.
        val unsupportedSyntax =
            Regex(
                "(?i)(?<![\\p{L}])(?:between|между)\\s+\\d|" +
                    "(?<![\\p{L}])(?:daily|weekly|monthly|yearly|annually|ежедневно|еженедельно|ежемесячно|ежегодно)\\s+" +
                    "(?:(?:at|в)\\s+\\d|today\\b|tomorrow\\b|next\\b|завтра\\b|\\d)",
            )
        val embeddedZone =
            Regex(
                "(?i)(?<![\\p{L}\\d])\\d{1,2}(?::\\d{2}){0,2}\\s*(?:[ap]\\.?m\\.?)?(?:\\s+|\\s*[\\[(]\\s*)" +
                    "(?:(?:UTC|GMT)(?:[+-]\\d{1,2}(?::?\\d{2})?)?|EST|EDT|CST|CDT|MST|MDT|PST|PDT|CET|CEST|BST|IST|JST|AEST|AEDT|" +
                    "[a-z_]+/[a-z_]+(?:/[a-z_]+)?)(?![\\p{L}\\d])",
            )
    }
}
