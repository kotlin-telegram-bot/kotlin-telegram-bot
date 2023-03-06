package com.github.kotlintelegrambot.echo

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId

fun main() {

    val bot = bot {

        token = "1850490646:AAFNNJ9YQ8wj6HwGWUNG9sv61g9_cm8Dzfk"

        dispatch {

            text {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id), text = text,
                    protectContent = true,
                disableNotification = false
                )
            }
        }
    }

    bot.startPolling()
}
