package com.github.kotlintelegrambot.updater

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.random.Random

class ExecutorLooperTest {

    private fun createExecutorLooper(coroutineDispatcher: CoroutineDispatcher) =
        ExecutorLooper(coroutineDispatcher)

    @Test
    fun `loops until quit is called`() = runTest {
        val sut = createExecutorLooper(StandardTestDispatcher(testScheduler))
        var count = 0
        val expectedCount: Int = Random.nextInt(1000)

        println("JcLog: Start test $expectedCount")

        sut.loop {
            count++

            if (count == expectedCount) {
                sut.quit()
            }
        }
        advanceUntilIdle()

        assertEquals(expectedCount, count)
    }
}
