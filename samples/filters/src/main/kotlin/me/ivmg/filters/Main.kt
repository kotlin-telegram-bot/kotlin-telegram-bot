package me.ivmg.filters

import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.filter
import me.ivmg.telegram.dispatcher.filters.Filter
import me.ivmg.telegram.dispatcher.filters.and
import me.ivmg.telegram.dispatcher.filters.or
import me.ivmg.telegram.dispatcher.text

fun main(args: Array<String>) {

    val one: Filter = { it.message?.text?.contains('1') ?: false }
    val two: Filter = { it.message?.text?.contains('2') ?: false }
    val three: Filter = { it.message?.text?.contains('3') ?: false }

    val bot = bot {

        token = "YOUR_API_KEY"

        dispatch {

            text(filter = one) { bot, update ->
                bot.sendMessage(chatId = update.message!!.chat.id, text = "1")
            }

            text(filter = two) { bot, update ->
                bot.sendMessage(chatId = update.message!!.chat.id, text = "2")
            }

            text(filter = one and two) { bot, update ->
                bot.sendMessage(chatId = update.message!!.chat.id, text = "1 and 2")
            }

            text(filter = one or two) { bot, update ->
                bot.sendMessage(chatId = update.message!!.chat.id, text = "1 or 2")
            }

            text(filter = one or (two and three)) { bot, update ->
                bot.sendMessage(chatId = update.message!!.chat.id, text = "1 or 2 and 3")
            }

            filter(two) {
                text { bot, update ->
                    bot.sendMessage(chatId = update.message!!.chat.id, text = "2 [filter block]")
                }

                text(filter = three) { bot, update ->
                    bot.sendMessage(chatId = update.message!!.chat.id, text = "2 and 3 [filter block + custom filter]")
                }

                filter(one) {
                    text { bot, update ->
                        bot.sendMessage(chatId = update.message!!.chat.id, text = "2 and 1 [nested filter blocks]")
                    }
                }
            }
        }
    }

    bot.startPolling()
}
