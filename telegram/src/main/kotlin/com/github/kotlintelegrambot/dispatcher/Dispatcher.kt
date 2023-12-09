package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.ErrorHandler
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.errors.TelegramError
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.DispatchableObject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class Dispatcher internal constructor(
    private val updatesChannel: Channel<DispatchableObject>,
    private val logLevel: LogLevel,
    private val coroutineDispatcher: CoroutineDispatcher,
) {

    internal lateinit var bot: Bot

    private val commandHandlers = linkedSetOf<Handler>()
    private val errorHandlers = arrayListOf<ErrorHandler>()

    private val scope: CoroutineScope = CoroutineScope(coroutineDispatcher)

    @Volatile private var job: Job? = null

    internal fun startCheckingUpdates() {
        job?.cancel()
        job = scope.launch { checkQueueUpdates() }
    }

    private suspend fun checkQueueUpdates() {
        while (true) {
            when (val item = updatesChannel.receive()) {
                is Update -> handleUpdate(item)
                is TelegramError -> handleError(item)
                else -> Unit
            }
            yield()
        }
    }

    fun addHandler(handler: Handler) {
        commandHandlers.add(handler)
    }

    fun removeHandler(handler: Handler) {
        commandHandlers.remove(handler)
    }

    fun addErrorHandler(errorHandler: ErrorHandler) {
        errorHandlers.add(errorHandler)
    }

    fun removeErrorHandler(errorHandler: ErrorHandler) {
        errorHandlers.remove(errorHandler)
    }

    private suspend fun handleUpdate(update: Update) {
        CoroutineScope(coroutineDispatcher).launch {
            commandHandlers
                .asSequence()
                .filter { !update.consumed }
                .filter { it.checkUpdate(update) }
                .map {
                    async {
                        try {
                            it.handleUpdate(bot, update)
                        } catch (throwable: Throwable) {
                            if (logLevel.shouldLogErrors()) {
                                throwable.printStackTrace()
                            }
                        }
                    }
                }
                .toList()
                .awaitAll()
        }
    }

    private fun handleError(error: TelegramError) {
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

    internal fun stopCheckingUpdates() {
        job?.cancel()
    }
}
