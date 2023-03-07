package com.github.kotlintelegrambot.updater

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

internal interface Looper {
    fun loop(loopBody: suspend () -> Unit)
    fun quit()
}

/**
 * [Looper] implementation that runs a given block of code in a loop with an [CoroutineDispatcher] (mostly
 * intended to run the loop in a different coroutine). The loop will stop if the coroutine running the
 * loop is interrupted or in the next iteration after the [quit] method is called.
 */
internal class CoroutineLooper(coroutineDispatcher: CoroutineDispatcher) : Looper {

    private val scope: CoroutineScope = CoroutineScope(coroutineDispatcher)
    @Volatile private var job: Job? = null

    override fun loop(loopBody: suspend () -> Unit) {
        job?.cancel()
        job = scope.launch { runLoop(loopBody) }
    }

    private suspend fun runLoop(loopBody: suspend () -> Unit) {
        while (true) {
            loopBody()
            yield()
        }
    }

    override fun quit() {
        job?.cancel()
    }
}
