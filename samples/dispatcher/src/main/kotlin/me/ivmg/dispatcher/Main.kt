package me.ivmg.dispatcher

import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.dispatcher.telegramError
import me.ivmg.telegram.dispatcher.text
import me.ivmg.telegram.network.fold
import okhttp3.logging.HttpLoggingInterceptor

fun main(args: Array<String>) {

    val bot = bot {

        token = "YOUR_API_KEY"
        timeout = 30
        logLevel = HttpLoggingInterceptor.Level.BODY

        dispatch {

            command("start") { bot, update->

                val result = bot.sendMessage(chatId = update.message!!.chat.id, text = "Bot started")

                result.fold({
                    // do something here with the response
                },{
                    // do something with the error (warn the user?)
                })
            }

            command("hello") { bot, update->

                val result = bot.sendMessage(chatId = update.message!!.chat.id, text = "Hello, world!")

                result.fold({
                    // do something here with the response
                },{
                    // do something with the error (warn the user?)
                })
            }

            text("ping") { bot, update ->
                bot.sendMessage(chatId = update.message!!.chat.id, text = "Pong")
            }

            telegramError { bot, telegramError ->
                println(telegramError.getErrorMessage())
            }
        }
    }

    bot.startPolling()
}