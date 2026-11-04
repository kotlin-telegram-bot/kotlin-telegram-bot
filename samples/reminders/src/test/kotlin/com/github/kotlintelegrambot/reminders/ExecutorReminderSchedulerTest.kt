package com.github.kotlintelegrambot.reminders

import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Clock
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class ExecutorReminderSchedulerTest {
    @Test
    fun `a due callback runs on a worker thread`() {
        val clock = Clock.systemUTC()
        ExecutorReminderScheduler(clock).use { scheduler ->
            val delivered = CountDownLatch(1)
            val thread = AtomicReference<Thread>()

            scheduler.schedule(clock.instant()) {
                thread.set(Thread.currentThread())
                delivered.countDown()
            }

            assertTrue(delivered.await(5, TimeUnit.SECONDS))
            assertNotEquals(Thread.currentThread(), thread.get())
        }
    }

    @Test
    fun `registering future work returns promptly and cancellation releases queued work`() {
        val clock = Clock.systemUTC()
        val executor = ScheduledThreadPoolExecutor(1)
        val caller = Executors.newSingleThreadExecutor()
        val delivered = CountDownLatch(1)
        try {
            ExecutorReminderScheduler(clock, executor).use { scheduler ->
                val registration =
                    caller.submit<Cancellation> {
                        scheduler.schedule(clock.instant().plus(Duration.ofDays(1))) { delivered.countDown() }
                    }
                val cancellation = registration.get(2, TimeUnit.SECONDS)
                assertTrue(delivered.count == 1L)

                cancellation.cancel()

                assertTrue(executor.queue.isEmpty())
            }
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS))
        } finally {
            caller.shutdownNow()
            executor.shutdownNow()
        }
    }

    @Test
    fun `shutdown discards pending work and rejects new work`() {
        val clock = Clock.systemUTC()
        val executor = ScheduledThreadPoolExecutor(1)
        val scheduler = ExecutorReminderScheduler(clock, executor)
        val delivered = CountDownLatch(1)
        scheduler.schedule(clock.instant().plus(Duration.ofDays(1))) { delivered.countDown() }

        scheduler.close()

        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS))
        assertTrue(executor.queue.isEmpty())
        assertTrue(delivered.count == 1L)
        assertThrows<RejectedExecutionException> { scheduler.schedule(clock.instant()) {} }
    }
}
