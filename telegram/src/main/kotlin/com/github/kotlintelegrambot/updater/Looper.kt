package com.github.kotlintelegrambot.updater

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

internal interface Looper {
    fun loop(block: suspend CoroutineScope.() -> Unit)
    fun quit()
    suspend fun awaitCancellation()
}

/**
 * [Looper] implementation that runs a given block of code in a loop with an [Executor] (mostly
 * intended to run the loop in a different thread). The loop will stop if the thread running the
 * loop is interrupted or in the next iteration after the [quit] method is called.
 */
internal class SuspendLooper(
    coroutineDispatcher: CoroutineDispatcher,
) : Looper {

    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    override fun loop(block: suspend CoroutineScope.() -> Unit) {
        coroutineScope.launch {
            while (isActive) {
                block()
            }
        }
    }

    override suspend fun awaitCancellation() {
        coroutineScope.coroutineContext.job.join()
    }

    override fun quit() {
        coroutineScope.cancel()
    }
}
