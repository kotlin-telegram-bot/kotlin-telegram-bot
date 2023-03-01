package com.github.kotlintelegrambot.updater

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.concurrent.Executor

internal interface Looper {
    fun loop(loopBody: suspend () -> Unit)
    fun quit()
}

/**
 * [Looper] implementation that runs a given block of code in a loop with an [Executor] (mostly
 * intended to run the loop in a different thread). The loop will stop if the thread running the
 * loop is interrupted or in the next iteration after the [quit] method is called.
 */
internal class ExecutorLooper(ioDispatcher: CoroutineDispatcher) : Looper {

    private val scope: CoroutineScope = CoroutineScope(ioDispatcher)
    private var job: Job? = null

    override fun loop(loopBody: suspend () -> Unit) {
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
