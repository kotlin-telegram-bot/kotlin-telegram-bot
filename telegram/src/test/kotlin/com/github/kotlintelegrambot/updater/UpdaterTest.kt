package com.github.kotlintelegrambot.updater

import anyUpdate
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.errors.RetrieveUpdatesError
import com.github.kotlintelegrambot.network.ApiClient
import com.github.kotlintelegrambot.types.DispatchableObject
import com.github.kotlintelegrambot.types.TelegramBotResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.util.concurrent.LinkedBlockingQueue

class UpdaterTest {

    private val mockUpdatesQueue = mockk<LinkedBlockingQueue<DispatchableObject>>(relaxUnitFun = true)
    private val mockApiClient = mockk<ApiClient>()

    private fun createUpdater(looper: Looper) = Updater(
        looper = looper,
        updatesQueue = mockUpdatesQueue,
        apiClient = mockApiClient,
        botTimeout = BOT_TIMEOUT
    )

    @Test
    fun `updates pagination in polling with several successful responses`() = runTest {
        val looper = BoundLooper(StandardTestDispatcher(testScheduler))
        val sut = createUpdater(looper)
        val updates1 = (1L until 3).map { anyUpdate(updateId = it) }
        val updates2 = emptyList<Update>()
        val updates3 = (3L until 6).map { anyUpdate(updateId = it) }
        val updates4 = (6L until 18).map { anyUpdate(updateId = it) }
        givenGetUpdatesResults(updates1, updates2, updates3, updates4)

        looper.loopIterations = 4
        sut.startPolling()
        advanceUntilIdle()

        verifyOrder {
            mockApiClient.getUpdates(offset = null, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
            mockApiClient.getUpdates(offset = 3, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
            mockApiClient.getUpdates(offset = 3, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
            mockApiClient.getUpdates(offset = 6, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
        }
    }

    @Test
    fun `updates pagination in polling with only errors`() = runTest {
        val looper = BoundLooper(StandardTestDispatcher(testScheduler))
        val sut = createUpdater(looper)
        val error1 = TelegramBotResult.Error.Unknown<List<Update>>(Exception())
        val error2 = TelegramBotResult.Error.HttpError<List<Update>>(400, "Not found")
        val error3 = TelegramBotResult.Error.TelegramApi<List<Update>>(523, "WAT")
        val error4 = TelegramBotResult.Error.InvalidResponse<List<Update>>(521, "WUT", null)
        givenGetUpdatesResults(
            error1,
            error2,
            error3,
            error4,
        )

        looper.loopIterations = 5
        sut.startPolling()
        advanceUntilIdle()

        verifyOrder {
            mockApiClient.getUpdates(offset = null, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
            mockApiClient.getUpdates(offset = null, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
            mockApiClient.getUpdates(offset = null, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
            mockApiClient.getUpdates(offset = null, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
        }
    }

    @Test
    fun `updates pagination in polling with mixed successes and errors`() = runTest {
        val looper = BoundLooper(StandardTestDispatcher(testScheduler))
        val sut = createUpdater(looper)
        val error1 = TelegramBotResult.Error.Unknown<List<Update>>(Exception())
        val updates1 = (1L until 3).map { anyUpdate(updateId = it) }
        val error2 = TelegramBotResult.Error.HttpError<List<Update>>(400, "Not found")
        val updates2 = emptyList<Update>()
        val updates3 = (3L until 6).map { anyUpdate(updateId = it) }
        givenGetUpdatesResults(
            error1,
            updates1.asResult(),
            updates2.asResult(),
            error2,
            updates3.asResult(),
        )

        looper.loopIterations = 5
        sut.startPolling()
        advanceUntilIdle()

        verifyOrder {
            mockApiClient.getUpdates(offset = null, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
            mockApiClient.getUpdates(offset = null, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
            mockApiClient.getUpdates(offset = 3, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
            mockApiClient.getUpdates(offset = 3, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
            mockApiClient.getUpdates(offset = 3, limit = null, timeout = BOT_TIMEOUT, allowedUpdates = null)
        }
    }

    @Test
    fun `queue updates in polling with several successful responses`() = runTest {
        val looper = BoundLooper(StandardTestDispatcher(testScheduler))
        val sut = createUpdater(looper)
        val updates1 = (1L until 3).map { anyUpdate(updateId = it) }
        val updates2 = emptyList<Update>()
        val updates3 = (3L until 6).map { anyUpdate(updateId = it) }
        val updates4 = (6L until 8).map { anyUpdate(updateId = it) }
        givenGetUpdatesResults(updates1, updates2, updates3, updates4)

        looper.loopIterations = 4
        sut.startPolling()
        advanceUntilIdle()

        verifyOrder {
            mockUpdatesQueue.put(updates1[0])
            mockUpdatesQueue.put(updates1[1])
            mockUpdatesQueue.put(updates3[0])
            mockUpdatesQueue.put(updates3[1])
            mockUpdatesQueue.put(updates3[2])
            mockUpdatesQueue.put(updates4[0])
            mockUpdatesQueue.put(updates4[1])
        }
    }

    @Test
    fun `queue updates and errors in polling with mixed successes and errors`() = runTest {
        val looper = BoundLooper(StandardTestDispatcher(testScheduler))
        val sut = createUpdater(looper)
        val error1 = TelegramBotResult.Error.Unknown<List<Update>>(Exception("I'm exceptional"))
        val updates1 = (1L until 3).map { anyUpdate(updateId = it) }
        val error2 = TelegramBotResult.Error.HttpError<List<Update>>(400, "Not found")
        val updates2 = emptyList<Update>()
        val updates3 = (3L until 6).map { anyUpdate(updateId = it) }
        givenGetUpdatesResults(
            error1,
            updates1.asResult(),
            error2,
            updates2.asResult(),
            updates3.asResult()
        )

        looper.loopIterations = 5
        sut.startPolling()
        advanceUntilIdle()

        val queuedErrors = mutableListOf<RetrieveUpdatesError>()
        verifyOrder {
            mockUpdatesQueue.put(capture(queuedErrors))
            mockUpdatesQueue.put(updates1[0])
            mockUpdatesQueue.put(updates1[1])
            mockUpdatesQueue.put(capture(queuedErrors))
            mockUpdatesQueue.put(updates3[0])
            mockUpdatesQueue.put(updates3[1])
            mockUpdatesQueue.put(updates3[2])
        }
        assertEquals("I'm exceptional", queuedErrors.last().getErrorMessage())
        assertEquals("400 Not found", queuedErrors.first().getErrorMessage())
    }

    @Test
    fun `queue error in polling with only errors`() = runTest {
        val looper = BoundLooper(StandardTestDispatcher(testScheduler))
        val sut = createUpdater(looper)
        val error1 = TelegramBotResult.Error.Unknown<List<Update>>(Exception("I'm exceptional"))
        val error2 = TelegramBotResult.Error.HttpError<List<Update>>(400, "Not found")
        val error3 = TelegramBotResult.Error.TelegramApi<List<Update>>(523, "WAT")
        val error4 = TelegramBotResult.Error.InvalidResponse<List<Update>>(521, "WUT", null)
        givenGetUpdatesResults(
            error1,
            error2,
            error3,
            error4,
        )

        looper.loopIterations = 5
        sut.startPolling()
        advanceUntilIdle()

        val queuedErrors = mutableListOf<RetrieveUpdatesError>()
        verify {
            mockUpdatesQueue.put(capture(queuedErrors))
            mockUpdatesQueue.put(capture(queuedErrors))
            mockUpdatesQueue.put(capture(queuedErrors))
            mockUpdatesQueue.put(capture(queuedErrors))
        }
        assertEquals("I'm exceptional", queuedErrors[0].getErrorMessage())
        assertEquals("400 Not found", queuedErrors[1].getErrorMessage())
        assertEquals("523 WAT", queuedErrors[2].getErrorMessage())
        assertEquals("521 WUT", queuedErrors[3].getErrorMessage())
    }

    private fun givenGetUpdatesResults(vararg result: List<Update>) {
        every {
            mockApiClient.getUpdates(any(), any(), any(), any())
        }.returnsMany(
            result.map { it.asResult() }
        )
    }

    private fun givenGetUpdatesResults(vararg result: TelegramBotResult<List<Update>>) {
        every {
            mockApiClient.getUpdates(any(), any(), any(), any())
        }.returnsMany(result.toList())
    }

    private fun List<Update>.asResult() = TelegramBotResult.Success(this)

    private companion object {
        const val BOT_TIMEOUT = 50
    }
}
