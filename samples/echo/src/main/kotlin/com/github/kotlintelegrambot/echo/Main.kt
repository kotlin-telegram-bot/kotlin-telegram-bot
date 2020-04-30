package com.github.kotlintelegrambot.echo

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text

fun main(args: Array<String>) {

    val bot = bot {

        token = "YOUR_API_KEY"

        dispatch {

            text { bot, update ->
                val text = update.message?.text ?: "Hello, World!"
                bot.sendMessage(chatId = update.message!!.chat.id, text = text)
            }
        }
    }

    bot.startPolling()
}
