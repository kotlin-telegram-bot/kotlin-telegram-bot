package com.github.kotlintelegrambot.updater

/**
 * Looper implementation for testing purposes. It will loop [loopIterations] times and then stop.
 */
class BoundLooper : Looper {

    var loopIterations = 0

    override fun loop(loopBody: () -> Unit) {
        repeat(loopIterations) {
            loopBody()
        }
    }

    override fun quit() {}
}
