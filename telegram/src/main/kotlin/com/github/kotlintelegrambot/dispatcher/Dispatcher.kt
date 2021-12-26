package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.ErrorHandler
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.errors.TelegramError
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.DispatchableObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

public class Dispatcher internal constructor(
    private val updatesChannel: Channel<DispatchableObject>,
    coroutineDispatcher: CoroutineDispatcher,
    private val logLevel: LogLevel,
) {

    internal lateinit var bot: Bot

    private val commandHandlers = mutableSetOf<Handler>()
    private val errorHandlers = mutableListOf<ErrorHandler>()
    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    internal fun launchCheckingUpdates() {
        coroutineScope.launch { checkQueueUpdates() }
    }

    internal suspend fun awaitCancellation() {
        coroutineScope.coroutineContext.job.join()
    }

    private suspend fun CoroutineScope.checkQueueUpdates() {
        while (isActive) {
            when (val item = updatesChannel.receive()) {
                is Update -> handleUpdate(item)
                is TelegramError -> handleError(item)
                else -> Unit
            }
        }
    }

    public fun addHandler(handler: Handler) {
        commandHandlers.add(handler)
    }

    public fun removeHandler(handler: Handler) {
        commandHandlers.remove(handler)
    }

    public fun addErrorHandler(errorHandler: ErrorHandler) {
        errorHandlers.add(errorHandler)
    }

    public fun removeErrorHandler(errorHandler: ErrorHandler) {
        errorHandlers.remove(errorHandler)
    }

    private suspend fun handleUpdate(update: Update) {
        commandHandlers
            .filter { it.checkUpdate(update) }
            .forEach {
                if (update.consumed) {
                    return
                }
                try {
                    it.handleUpdate(bot, update)
                } catch (throwable: Throwable) {
                    if (logLevel.shouldLogErrors()) {
                        throwable.printStackTrace()
                    }
                }
            }
    }

    private suspend fun handleError(error: TelegramError) {
        errorHandlers.forEach { handleError ->
            try {
                handleError(bot, error)
            } catch (throwable: Throwable) {
                if (logLevel.shouldLogErrors()) {
                    throwable.printStackTrace()
                }
            }
        }
    }

    internal fun cancelCheckingUpdates() {
        coroutineScope.cancel()
    }
}
