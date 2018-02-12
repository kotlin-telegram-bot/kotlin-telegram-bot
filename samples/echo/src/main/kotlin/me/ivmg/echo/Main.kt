package me.ivmg.echo

import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.text

fun main(args: Array<String>) {

    val bot = bot {

        token = "YOUR_API_KEY"

        dispatch {

            text { bot, update ->
                bot.sendMessage(chatId = update.message!!.chat.id, text = update.message!!.text!!)
            }
        }
    }

    bot.startPolling()
}