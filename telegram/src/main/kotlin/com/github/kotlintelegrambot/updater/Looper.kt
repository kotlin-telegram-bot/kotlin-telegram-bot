package com.github.kotlintelegrambot.updater

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

internal interface Looper {
    fun launchLoop(block: suspend CoroutineScope.() -> Unit)
    fun cancelLoop()
    suspend fun awaitCancellation()
}

internal class SuspendLooper(
    coroutineDispatcher: CoroutineDispatcher,
) : Looper {

    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    override fun launchLoop(block: suspend CoroutineScope.() -> Unit) {
        coroutineScope.launch {
            while (isActive) {
                block()
            }
        }
    }

    override suspend fun awaitCancellation() {
        coroutineScope.coroutineContext.job.join()
    }

    override fun cancelLoop() {
        coroutineScope.cancel()
    }
}
