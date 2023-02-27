import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.handlers.HandleText
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandler
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.DispatchableObject
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.BlockingQueue

class DispatcherTest {

    private val botMock = mockk<Bot>()
    private val blockingQueueMock = mockk<BlockingQueue<DispatchableObject>>()

    private fun createDispatcher(coroutineDispatcher: CoroutineDispatcher) = Dispatcher(
        blockingQueueMock,
        LogLevel.None,
        coroutineDispatcher,
    ).apply {
        bot = botMock
    }

    private suspend fun mockHandler(): Handler {
        return mockk {
            coEvery { handleUpdate(any(), any()) } just runs
            every { checkUpdate(any()) } returns true
        }
    }

    private companion object {
        const val ANY_TEXT = "Valar Morghulis"
    }

    @Test
    fun `updates are dispatched to handlers when updates check starts and there are some updates`() = runTest {
        val mockHandler = mockHandler()
        val sut = createDispatcher(StandardTestDispatcher(testScheduler))
        sut.addHandler(mockHandler)
        val anyUpdate = anyUpdate()
        every { blockingQueueMock.take() } returns anyUpdate andThenThrows InterruptedException()

        try {
            sut.startCheckingUpdates()
            advanceUntilIdle()
        } catch (exception: InterruptedException) {
        } finally {
            coVerify(exactly = 1) { mockHandler.handleUpdate(botMock, anyUpdate) }
        }
    }

    @Test
    fun `handlers are not called after update is consumed`() = runTest {
        val sut = createDispatcher(StandardTestDispatcher(testScheduler))
        val anyMessageWithText = anyUpdate(message = anyMessage(text = ANY_TEXT))
        val firstHandler = TextHandler(
            text = null,
            handleText = {
                if (text == ANY_TEXT) {
                    update.consume()
                }
            }
        )

        val handlerCallbackMock = mockk<HandleText>(relaxed = true)
        val secondHandler = TextHandler(text = null, handleText = handlerCallbackMock)

        sut.addHandler(firstHandler)
        sut.addHandler(secondHandler)

        every { blockingQueueMock.take() } returns anyMessageWithText andThenThrows InterruptedException()
        try {
            sut.startCheckingUpdates()
            advanceUntilIdle()
        } catch (exception: InterruptedException) {
        } finally {
            assertTrue(anyMessageWithText.consumed)
            coVerify(exactly = 0) { handlerCallbackMock(any()) }
        }
    }

    @Test
    fun `test that handlers from different groups are called in consistent order`() = runTest {
        val sut = createDispatcher(StandardTestDispatcher(testScheduler))
        val mockHandler1 = mockHandler()
        val mockHandler2 = mockHandler()
        val mockHandler3 = mockHandler()
        sut.addHandler(mockHandler1)
        sut.addHandler(mockHandler2)
        sut.addHandler(mockHandler3)

        val anyUpdate = anyUpdate()
        every { blockingQueueMock.take() } returns anyUpdate andThenThrows InterruptedException()

        try {
            sut.startCheckingUpdates()
            advanceUntilIdle()
        } catch (exception: InterruptedException) {
        } finally {
            coVerifyOrder {
                mockHandler1.handleUpdate(botMock, anyUpdate)
                mockHandler2.handleUpdate(botMock, anyUpdate)
                mockHandler3.handleUpdate(botMock, anyUpdate)
            }
        }
    }
}
