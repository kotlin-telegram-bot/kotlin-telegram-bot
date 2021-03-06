package com.github.kotlintelegrambot.updater

import java.util.concurrent.Executor

internal interface Looper {
    fun loop(loopBody: () -> Unit)
    fun quit()
}

/**
 * [Looper] implementation that runs a given block of code in a loop with an [Executor] (mostly
 * intended to run the loop in a different thread). The loop will stop if the thread running the
 * loop is interrupted or in the next iteration after the [quit] method is called.
 */
internal class ExecutorLooper(
    private val loopExecutor: Executor,
) : Looper {

    @Volatile private var isLooping = false

    override fun loop(loopBody: () -> Unit) {
        isLooping = true
        loopExecutor.execute { runLoop(loopBody) }
    }

    private fun runLoop(loopBody: () -> Unit) {
        while (!Thread.interrupted() && isLooping) {
            loopBody()
        }
    }

    override fun quit() {
        isLooping = false
    }
}
