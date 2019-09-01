package me.ivmg.telegram.dispatcher

import me.ivmg.telegram.Bot
import me.ivmg.telegram.ContactHandleUpdate
import me.ivmg.telegram.HandleError
import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.LocationHandleUpdate
import me.ivmg.telegram.CommandHandleUpdate
import me.ivmg.telegram.dispatcher.handlers.CallbackQueryHandler
import me.ivmg.telegram.dispatcher.handlers.ChannelHandler
import me.ivmg.telegram.dispatcher.handlers.CheckoutHandler
import me.ivmg.telegram.dispatcher.handlers.CommandHandler
import me.ivmg.telegram.dispatcher.handlers.ContactHandler
import me.ivmg.telegram.dispatcher.handlers.Handler
import me.ivmg.telegram.dispatcher.handlers.LocationHandler
import me.ivmg.telegram.dispatcher.handlers.MessageHandler
import me.ivmg.telegram.dispatcher.handlers.TextHandler
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.errors.TelegramError
import me.ivmg.telegram.extensions.filters.Filter
import me.ivmg.telegram.types.DispatchableObject
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

fun Dispatcher.message(filter: Filter, handleUpdate: HandleUpdate) {
    addHandler(MessageHandler(handleUpdate, filter))
}

fun Dispatcher.command(command: String, body: HandleUpdate) {
    addHandler(CommandHandler(command, body))
}

fun Dispatcher.command(command: String, body: CommandHandleUpdate) {
    addHandler(CommandHandler(command, body))
}

fun Dispatcher.text(text: String? = null, body: HandleUpdate) {
    addHandler(TextHandler(text, body))
}

fun Dispatcher.callbackQuery(data: String? = null, body: HandleUpdate) {
    addHandler(CallbackQueryHandler(callbackData = data, handler = body))
}

fun Dispatcher.callbackQuery(callbackQueryHandler: CallbackQueryHandler) {
    addHandler(callbackQueryHandler)
}

fun Dispatcher.contact(handleUpdate: ContactHandleUpdate) {
    addHandler(ContactHandler(handleUpdate))
}

fun Dispatcher.location(handleUpdate: LocationHandleUpdate) {
    addHandler(LocationHandler(handleUpdate))
}

fun Dispatcher.telegramError(body: HandleError) {
    addErrorHandler(body)
}

fun Dispatcher.preCheckoutQuery(body: HandleUpdate) {
    addHandler(CheckoutHandler(body))
}

fun Dispatcher.channel(body: HandleUpdate) {
    addHandler(ChannelHandler(body))
}

class Dispatcher {

    lateinit var bot: Bot

    val updatesQueue: BlockingQueue<DispatchableObject> = LinkedBlockingQueue<DispatchableObject>()

    private val commandHandlers = mutableMapOf<String, ArrayList<Handler>>()
    private val errorHandlers = arrayListOf<HandleError>()

    fun startCheckingUpdates() {
        checkQueueUpdates()
    }

    private fun checkQueueUpdates() {
        while (!Thread.currentThread().isInterrupted) {
            val item = updatesQueue.take()
            if (item != null) {
                if (item is Update) handleUpdate(item)
                else if (item is TelegramError) handleError(item)
            }
        }
    }

    fun addHandler(handler: Handler) {
        var handlers = commandHandlers[handler.groupIdentifier]

        if (handlers == null) {
            handlers = arrayListOf()
            commandHandlers[handler.groupIdentifier] = handlers
        }

        handlers.add(handler)
    }

    fun removeHandler(handler: Handler) {
        commandHandlers[handler.groupIdentifier]?.remove(handler)
    }

    fun addErrorHandler(errorHandler: HandleError) {
        errorHandlers.add(errorHandler)
    }

    fun removeErrorHandler(errorHandler: HandleError) {
        errorHandlers.remove(errorHandler)
    }

    private fun handleUpdate(update: Update) {
        for (group in commandHandlers) {
            group.value
                .filter { it.checkUpdate(update) }
                .forEach { it.handlerCallback(bot, update) }
        }
    }

    private fun handleError(error: TelegramError) {
        errorHandlers.forEach {
            it(bot, error)
        }
    }
}
