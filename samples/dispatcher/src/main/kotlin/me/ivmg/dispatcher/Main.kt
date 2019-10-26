package me.ivmg.dispatcher

import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.callbackQuery
import me.ivmg.telegram.dispatcher.channel
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.dispatcher.contact
import me.ivmg.telegram.dispatcher.handlers.CallbackQueryHandler
import me.ivmg.telegram.dispatcher.location
import me.ivmg.telegram.dispatcher.message
import me.ivmg.telegram.dispatcher.photos
import me.ivmg.telegram.dispatcher.telegramError
import me.ivmg.telegram.dispatcher.text
import me.ivmg.telegram.entities.InlineKeyboardButton
import me.ivmg.telegram.entities.InlineKeyboardMarkup
import me.ivmg.telegram.entities.KeyboardButton
import me.ivmg.telegram.entities.KeyboardReplyMarkup
import me.ivmg.telegram.entities.ReplyKeyboardRemove
import me.ivmg.telegram.extensions.filters.Filter
import me.ivmg.telegram.network.fold
import okhttp3.logging.HttpLoggingInterceptor

fun main(args: Array<String>) {

    val bot = bot {

        token = "YOUR_API_KEY"
        timeout = 30
        logLevel = HttpLoggingInterceptor.Level.BODY

        dispatch {
            message(Filter.Sticker) { bot, update ->
                bot.sendMessage(update.message!!.chat.id, text = "You have received an awesome sticker \\o/")
            }

            message(Filter.Reply or Filter.Forward) { bot, update ->
                bot.sendMessage(update.message!!.chat.id, text = "someone is replying or forwarding messages ...")
            }

            command("start") { bot, update ->

                val result = bot.sendMessage(chatId = update.message!!.chat.id, text = "Bot started")

                result.fold({
                    // do something here with the response
                }, {
                    // do something with the error
                })
            }

            command("hello") { bot, update ->

                val result = bot.sendMessage(chatId = update.message!!.chat.id, text = "Hello, world!")

                result.fold({
                    // do something here with the response
                }, {
                    // do something with the error
                })
            }

            command("commandWithArgs") { bot, update, args ->
                val joinedArgs = args.joinToString()
                val response = if (!joinedArgs.isNullOrBlank()) joinedArgs else "There is no text apart from command!"
                bot.sendMessage(chatId = update.message!!.chat.id, text = response)
            }

            command("inlineButtons") { bot, update ->
                val chatId = update.message?.chat?.id ?: return@command

                val inlineKeyboardMarkup = InlineKeyboardMarkup(generateButtons())
                bot.sendMessage(chatId = chatId, text = "Hello, inline buttons!", replyMarkup = inlineKeyboardMarkup)
            }

            command("userButtons") { bot, update ->
                val chatId = update.message?.chat?.id ?: return@command

                val keyboardMarkup = KeyboardReplyMarkup(keyboard = generateUsersButton(), resizeKeyboard = true)
                bot.sendMessage(chatId = chatId, text = "Hello, users buttons!", replyMarkup = keyboardMarkup)
            }

            callbackQuery("testButton") { bot, update ->
                update.callbackQuery?.let {
                    val chatId = it.message?.chat?.id ?: return@callbackQuery
                    bot.sendMessage(chatId = chatId, text = it.data)
                }
            }

            callbackQuery(createAlertCallbackQueryHandler { bot, update ->
                update.callbackQuery?.let {
                    val chatId = it.message?.chat?.id ?: return@createAlertCallbackQueryHandler
                    bot.sendMessage(chatId = chatId, text = it.data)
                }
            })

            text("ping") { bot, update ->
                bot.sendMessage(chatId = update.message!!.chat.id, text = "Pong")
            }

            location { bot, update, location ->
                val chatId = update.message?.chat?.id ?: return@location
                bot.sendMessage(
                    chatId = chatId,
                    text = "Your location is (${location.latitude}, ${location.longitude})",
                    replyMarkup = ReplyKeyboardRemove()
                )
            }

            contact { bot, update, contact ->
                val chatId = update.message?.chat?.id ?: return@contact
                bot.sendMessage(
                    chatId = chatId,
                    text = "Hello, ${contact.firstName} ${contact.lastName}",
                    replyMarkup = ReplyKeyboardRemove()
                )
            }

            channel { bot, update ->
                // Handle channel update
            }

            photos { bot, update, _ ->
                val chatId = update.message?.chat?.id ?: return@photos
                bot.sendMessage(
                    chatId = chatId,
                    text = "Wowww, awesome photos!!! :P"
                )
            }

            telegramError { _, telegramError ->
                println(telegramError.getErrorMessage())
            }
        }
    }

    bot.startPolling()
}

fun generateUsersButton(): List<List<KeyboardButton>> {
    return listOf(
        listOf(KeyboardButton("Request location (not supported on desktop)", requestLocation = true)),
        listOf(KeyboardButton("Request contact", requestContact = true))
    )
}

fun createAlertCallbackQueryHandler(handler: HandleUpdate): CallbackQueryHandler {
    return CallbackQueryHandler(
        callbackData = "showAlert",
        callbackAnswerText = "HelloText",
        callbackAnswerShowAlert = true,
        handler = handler
    )
}

fun generateButtons(): List<List<InlineKeyboardButton>> {
    return listOf(
        listOf(InlineKeyboardButton(text = "Test Inline Button", callbackData = "testButton")),
        listOf(InlineKeyboardButton(text = "Show alert", callbackData = "showAlert"))
    )
}
