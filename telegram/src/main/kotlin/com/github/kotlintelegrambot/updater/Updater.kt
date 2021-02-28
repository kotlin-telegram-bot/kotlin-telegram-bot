package com.github.kotlintelegrambot.updater

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.types.DispatchableObject
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor

internal class Updater(
    private val updatesQueue: BlockingQueue<DispatchableObject>,
    private val updatesExecutor: Executor,
) {
    private var lastUpdateId = 0L
    @Volatile private var stopped = false

    lateinit var bot: Bot

    internal fun startPolling() {
        stopped = false
        updatesExecutor.execute { updaterStartPolling() }
    }

    internal fun stopPolling() {
        stopped = true
    }

    private fun updaterStartPolling() {
        while (!Thread.currentThread().isInterrupted && !stopped) {
            val items = bot.getUpdates(lastUpdateId)

            if (items.isEmpty()) continue

            items.forEach(updatesQueue::put)

            val lastUpdate = try {
                items.last({ it is Update }) as Update
            } catch (e: NoSuchElementException) {
                continue
            }

            lastUpdateId = lastUpdate.updateId + 1
        }
    }
}
