package com.github.kotlintelegrambot.updater

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

/**
 * Looper implementation for testing purposes. It will loop [loopIterations] times and then stop.
 */
class BoundLooper : Looper {

    var loopIterations = 0

    override fun launchLoop(block: suspend CoroutineScope.() -> Unit) = runBlocking {
        repeat(loopIterations) {
            block()
        }
    }

    override fun cancelLoop() {}
    override suspend fun awaitCancellation() = Unit
}
