package com.github.kotlintelegrambot.echo

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId

fun main() {
    val bot = bot {
        token = "YOUR_API_KEY"

        dispatch {
            text {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    messageThreadId = message.messageThreadId,
                    text = text,
                    protectContent = true,
                    disableNotification = false,
                )
            }
        }
    }

    bot.startPolling()
}
