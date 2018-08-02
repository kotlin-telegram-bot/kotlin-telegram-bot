package me.ivmg.telegram.dispatcher

import me.ivmg.telegram.Bot
import me.ivmg.telegram.ContactHandleUpdate
import me.ivmg.telegram.HandleError
import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.LocationHandleUpdate
import me.ivmg.telegram.CommandHandleUpdate
import me.ivmg.telegram.dispatcher.filters.Filter
import me.ivmg.telegram.dispatcher.filters.and
import me.ivmg.telegram.dispatcher.handlers.CallbackQueryHandler
import me.ivmg.telegram.dispatcher.handlers.CheckoutHandler
import me.ivmg.telegram.dispatcher.handlers.CommandHandler
import me.ivmg.telegram.dispatcher.handlers.ContactHandler
import me.ivmg.telegram.dispatcher.handlers.Handler
import me.ivmg.telegram.dispatcher.handlers.LocationHandler
import me.ivmg.telegram.dispatcher.handlers.TextHandler
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.errors.TelegramError
import me.ivmg.telegram.types.DispatchableObject
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

fun Dispatcher.command(command: String, filter: Filter? = null, body: HandleUpdate) {
    addHandler(CommandHandler(command, combineWithCurrentFilter(filter), body))
}

fun Dispatcher.command(command: String, filter: Filter? = null, body: CommandHandleUpdate) {
    addHandler(CommandHandler(command, combineWithCurrentFilter(filter), body))
}

fun Dispatcher.text(text: String? = null, filter: Filter? = null, body: HandleUpdate) {
    addHandler(TextHandler(text, combineWithCurrentFilter(filter), body))
}

fun Dispatcher.callbackQuery(data: String? = null, filter: Filter? = null, body: HandleUpdate) {
    addHandler(CallbackQueryHandler(callbackData = data, filter = combineWithCurrentFilter(filter), handler = body))
}

fun Dispatcher.callbackQuery(callbackQueryHandler: CallbackQueryHandler) {
    addHandler(callbackQueryHandler)
}

fun Dispatcher.contact(filter: Filter? = null, handleUpdate: ContactHandleUpdate) {
    addHandler(ContactHandler(combineWithCurrentFilter(filter), handleUpdate))
}

fun Dispatcher.location(filter: Filter? = null, handleUpdate: LocationHandleUpdate) {
    addHandler(LocationHandler(combineWithCurrentFilter(filter), handleUpdate))
}

fun Dispatcher.telegramError(body: HandleError) {
    addErrorHandler(body)
}

fun Dispatcher.preCheckoutQuery(filter: Filter? = null, body: HandleUpdate) {
    addHandler(CheckoutHandler(combineWithCurrentFilter(filter), body))
}

fun Dispatcher.filter(filter: Filter, body: Dispatcher.() -> Unit): Dispatcher {
    val previousFilter = this.currentFilter

    if (previousFilter != null)
        this.currentFilter = previousFilter and filter
    else
        this.currentFilter = filter

    this.body()

    this.currentFilter = previousFilter

    return this
}

class Dispatcher {

    lateinit var bot: Bot

    val updatesQueue: BlockingQueue<DispatchableObject> = LinkedBlockingQueue<DispatchableObject>()

    private val commandHandlers = mutableMapOf<String, ArrayList<Handler>>()
    private val errorHandlers = arrayListOf<HandleError>()

    var currentFilter: Filter? = null

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
                .filter { it.checkUpdate(update) && (it.filter?.invoke(update) ?: true) }
                .forEach { it.handlerCallback(bot, update) }
        }
    }

    private fun handleError(error: TelegramError) {
        errorHandlers.forEach {
            it(bot, error)
        }
    }

    fun combineWithCurrentFilter(filter: Filter?): Filter? = currentFilter.let { currentFilter ->
        return when {
            filter == null -> currentFilter
            currentFilter == null -> filter
            else -> currentFilter and filter
        }
    }
}