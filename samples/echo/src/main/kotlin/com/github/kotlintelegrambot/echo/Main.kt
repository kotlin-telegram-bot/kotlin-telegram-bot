package com.github.kotlintelegrambot.echo

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.handlers.requireChatId
import com.github.kotlintelegrambot.dispatcher.text

fun main() {

    val bot = bot {

        token = "YOUR_API_KEY"

        dispatch {

            text {
                bot.sendMessage(chatId = requireChatId(), text = text)
            }
        }
    }

    bot.startPolling(wait = true)
}
