package me.ivmg.telegram

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import me.ivmg.telegram.dispatcher.Dispatcher
import me.ivmg.telegram.entities.Update

class Updater {
    private val executor: Executor = Executors.newCachedThreadPool()
    private var lastUpdateId = 0L
    private var stopped = false

    lateinit var bot: Bot
    val dispatcher = Dispatcher()

    fun startPolling() {
        stopped = false
        executor.execute { dispatcher.startCheckingUpdates() }
        executor.execute { updaterStartPolling() }
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
        dispatcher.stopCheckingUpdates()
    }
}
