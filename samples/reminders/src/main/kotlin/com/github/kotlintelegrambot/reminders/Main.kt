package com.github.kotlintelegrambot.reminders

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.logging.LogLevel
import java.time.Clock
import java.time.ZoneId

fun main() {
    val token =
        requireNotNull(System.getenv("TELEGRAM_BOT_TOKEN")?.takeIf { it.isNotBlank() }) {
            "Set TELEGRAM_BOT_TOKEN before starting this sample."
        }
    val timeZone = ZoneId.of(requireNotNull(System.getenv("REMINDER_TIME_ZONE")) { "Set REMINDER_TIME_ZONE, for example Europe/Berlin or UTC." })
    val clock = Clock.systemUTC()
    val scheduler = ExecutorReminderScheduler(clock)
    lateinit var telegramBot: Bot
    val service =
        ReminderService(clock, timeZone, scheduler) { chatId, reminder ->
            // One plain-text delivery attempt. Do not print errors, tokens, or message contents.
            telegramBot.sendMessage(ChatId.fromId(chatId), text = "Reminder: ${reminder.title}")
        }
    val commands = ReminderCommands(service)
    telegramBot =
        bot {
            this.token = token
            logLevel = LogLevel.None
            dispatch {
                listOf("remind", "confirm", "cancel").forEach { name ->
                    command(name) {
                        val reply = commands.handle(message.chat.id, message.chat.type, name, args.joinToString(" "))
                        bot.sendMessage(ChatId.fromId(message.chat.id), text = reply)
                    }
                }
            }
        }
    Runtime.getRuntime().addShutdownHook(
        Thread {
            try {
                telegramBot.stopPolling()
            } finally {
                service.close()
                scheduler.close()
            }
        },
    )
    telegramBot.startPolling()
}
