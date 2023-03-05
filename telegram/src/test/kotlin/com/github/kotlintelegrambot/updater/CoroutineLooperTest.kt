package com.github.kotlintelegrambot.updater

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.random.Random

class CoroutineLooperTest {

    private fun createCoroutineLooper(coroutineDispatcher: CoroutineDispatcher) =
        CoroutineLooper(coroutineDispatcher)

    @Test
    fun `loops until quit is called`() = runTest {
        val sut = createCoroutineLooper(StandardTestDispatcher(testScheduler))
        var count = 0
        val expectedCount: Int = Random.nextInt(1000)

        sut.loop {
            count++

            if (count == expectedCount) {
                sut.quit()
            }
        }
        advanceUntilIdle()

        assertEquals(expectedCount, count)
    }

    @Test
    fun `loops until an exception is thrown`() = runTest {
        val sut = createCoroutineLooper(StandardTestDispatcher(testScheduler))
        var count = 0
        val expectedCount: Int = Random.nextInt(1000)

        try {
            sut.loop {
                count++

                if (count == expectedCount) {
                    throw RuntimeException("oops")
                }
            }
        } catch (testException: RuntimeException) {
        } finally {
            advanceUntilIdle()
            assertEquals(expectedCount, count)
        }
    }
}
