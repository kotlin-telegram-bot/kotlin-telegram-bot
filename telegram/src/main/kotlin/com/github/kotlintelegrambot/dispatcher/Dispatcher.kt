package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.ErrorHandler
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.errors.TelegramError
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.DispatchableObject
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor

class Dispatcher internal constructor(
    private val updatesQueue: BlockingQueue<DispatchableObject>,
    private val updatesExecutor: Executor,
    private val logLevel: LogLevel,
) {

    internal lateinit var bot: Bot

    private val commandHandlers = linkedSetOf<Handler>()
    private val errorHandlers = arrayListOf<ErrorHandler>()

    @Volatile private var stopped = false

    internal fun startCheckingUpdates() {
        stopped = false
        updatesExecutor.execute { checkQueueUpdates() }
    }

    private fun checkQueueUpdates() {
        while (!Thread.currentThread().isInterrupted && !stopped) {
            val item = updatesQueue.take()
            when (item) {
                is Update -> handleUpdate(item)
                is TelegramError -> handleError(item)
                else -> Unit
            }
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

    private fun handleUpdate(update: Update) {
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
        stopped = true
    }
}
