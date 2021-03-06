package com.github.kotlintelegrambot.updater

import com.github.kotlintelegrambot.testutils.DirectExecutor
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

class ExecutorLooperTest {

    private val sut = ExecutorLooper(
        loopExecutor = DirectExecutor(),
    )

    @Test
    fun `loops until quit is called`() {
        var count = 0

        sut.loop {
            count++

            if (count == 13) {
                sut.quit()
            }
        }

        assertEquals(13, count)
    }

    @Test
    fun `loops until thread is interrupted`() {
        var count = 0

        sut.loop {
            count++

            if (count == 13) {
                Thread.currentThread().interrupt()
            }
        }

        assertEquals(13, count)
    }

    @Test
    fun `loops until an exception is thrown`() {
        var count = 0

        try {
            sut.loop {
                count++

                if (count == 13) {
                    throw RuntimeException("oops")
                }
            }
        } catch (testException: RuntimeException) {
        } finally {
            assertEquals(13, count)
        }
    }
}
