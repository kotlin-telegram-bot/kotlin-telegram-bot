package com.github.kotlintelegrambot.updater

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Looper implementation for testing purposes. It will loop [loopIterations] times and then stop.
 */
class BoundLooper(coroutineDispatcher: CoroutineDispatcher) : Looper {

    private val scope: CoroutineScope = CoroutineScope(coroutineDispatcher)
    var loopIterations = 0

    override fun loop(loopBody: suspend () -> Unit) {
        scope.launch {
            repeat(loopIterations) {
                loopBody()
            }
        }
    }

    override fun quit() {}
}
