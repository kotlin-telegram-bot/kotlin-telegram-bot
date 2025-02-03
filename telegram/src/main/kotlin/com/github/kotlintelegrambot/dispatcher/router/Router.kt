package com.github.kotlintelegrambot.dispatcher.router

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.ErrorHandler
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.errors.TelegramError
import com.github.kotlintelegrambot.logging.LogLevel

fun router(body: Router.() -> Unit) = Router().apply(body)

open class Router internal constructor() {
    protected val handlers = linkedSetOf<Handler>()
    protected val children = linkedSetOf<Router>()
    protected val errorHandlers = arrayListOf<ErrorHandler>()

    protected suspend fun handleUpdate(
        bot: Bot,
        logLevel: LogLevel,
        update: Update,
    ) {
        handlers
            .asSequence()
            .filter { it.checkUpdate(update) }
            .forEach {
                try {
                    it.handleUpdate(bot, update)

                    if (update.consumed) return
                } catch (throwable: Throwable) {
                    if (logLevel.shouldLogErrors()) {
                        throwable.printStackTrace()
                    }
                }
            }

        children
            .asSequence()
            .forEach { child ->
                handleUpdate(bot, logLevel, update)
                if (update.consumed) return
            }
    }

    protected fun handleError(
        bot: Bot,
        logLevel: LogLevel,
        error: TelegramError,
    ) {
        errorHandlers.forEach { handleError ->
            try {
                handleError(bot, error)
            } catch (throwable: Throwable) {
                if (logLevel.shouldLogErrors()) {
                    throwable.printStackTrace()
                }
            }
        }

        children.forEach {
            it.handleError(bot, logLevel, error)
        }
    }

    fun getHandlers() = handlers.toList()

    fun getChildren() = children.toList()

    fun getErrorHandlers() = errorHandlers.toList()

    fun addHandler(handler: Handler) {
        handlers.add(handler)
    }

    fun removeHandler(handler: Handler) {
        handlers.remove(handler)
    }

    fun addErrorHandler(errorHandler: ErrorHandler) {
        errorHandlers.add(errorHandler)
    }

    fun removeErrorHandler(errorHandler: ErrorHandler) {
        errorHandlers.remove(errorHandler)
    }

    fun includeRouter(router: Router) {
        children.add(router)
    }

    fun excludeRouter(router: Router) {
        children.remove(router)
    }
}
