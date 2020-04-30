package com.github.kotlintelegrambot.updater

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.entities.Update
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Updater {
    private val executor: Executor = Executors.newCachedThreadPool()
    private var lastUpdateId = 0L
    private var stopped = false

    lateinit var bot: Bot
    val dispatcher = Dispatcher()

    fun startPolling() {
        startCheckingUpdates()
        stopped = false
        executor.execute { updaterStartPolling() }
    }

    fun startCheckingUpdates() {
        executor.execute { dispatcher.startCheckingUpdates() }
    }

    private fun updaterStartPolling() {
        while (!Thread.currentThread().isInterrupted && !stopped) {
            val items = bot.getUpdates(lastUpdateId)

            if (items.isEmpty()) continue

            items.forEach {
                dispatcher.updatesQueue.put(it)
            }

            val lastUpdate = try {
                items.last({ it is Update }) as Update
            } catch (e: NoSuchElementException) {
                continue
            }

            lastUpdateId = lastUpdate.updateId + 1
        }
    }

    internal fun stopPolling() {
        stopped = true
        stopCheckingUpdates()
    }

    fun stopCheckingUpdates() {
        dispatcher.stopCheckingUpdates()
    }
}
