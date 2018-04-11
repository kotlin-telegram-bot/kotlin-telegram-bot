package me.ivmg.telegram

import me.ivmg.telegram.dispatcher.Dispatcher
import me.ivmg.telegram.entities.Update
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Updater {
    private val executor: Executor = Executors.newCachedThreadPool()
    private var lastUpdateId = 0L

    lateinit var bot: Bot
    val dispatcher = Dispatcher()

    fun startPolling() {
        executor.execute { dispatcher.startCheckingUpdates() }
        executor.execute { updaterStartPolling() }
    }

    private fun updaterStartPolling() {
        while (!Thread.currentThread().isInterrupted) {
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
}