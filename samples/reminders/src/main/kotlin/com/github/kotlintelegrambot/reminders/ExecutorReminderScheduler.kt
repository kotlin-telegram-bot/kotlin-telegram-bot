package com.github.kotlintelegrambot.reminders

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ExecutorReminderScheduler(
    private val clock: Clock,
    private val executor: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1),
) : ReminderScheduler,
    AutoCloseable {
    init {
        executor.removeOnCancelPolicy = true
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false)
    }

    override fun schedule(
        at: Instant,
        action: () -> Unit,
    ): Cancellation {
        val delay = Duration.between(clock.instant(), at).toMillis().coerceAtLeast(0)
        val future = executor.schedule(action, delay, TimeUnit.MILLISECONDS)
        return Cancellation { future.cancel(false) }
    }

    override fun close() {
        executor.shutdownNow()
    }
}
