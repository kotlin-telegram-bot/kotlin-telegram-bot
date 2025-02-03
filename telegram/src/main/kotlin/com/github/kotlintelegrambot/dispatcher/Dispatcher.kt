package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.router.Router
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.errors.TelegramError
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.DispatchableObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class Dispatcher internal constructor(
    private val updatesChannel: Channel<DispatchableObject>,
    private val logLevel: LogLevel,
    coroutineDispatcher: CoroutineDispatcher,
) : Router() {
    internal lateinit var bot: Bot

    private val scope: CoroutineScope = CoroutineScope(coroutineDispatcher)

    @Volatile private var job: Job? = null

    internal fun startCheckingUpdates() {
        job?.cancel()
        job = scope.launch { checkQueueUpdates() }
    }

    private suspend fun checkQueueUpdates() {
        while (true) {
            when (val item = updatesChannel.receive()) {
                is Update -> handleUpdate(bot, logLevel, item)
                is TelegramError -> handleError(bot, logLevel, item)
                else -> Unit
            }
            yield()
        }
    }

    internal fun stopCheckingUpdates() {
        job?.cancel()
    }
}
