import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.handlers.HandleText
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandler
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.testutils.DirectExecutor
import com.github.kotlintelegrambot.types.DispatchableObject
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.BlockingQueue

class DispatcherTest {

    private val botMock = mockk<Bot>()
    private val blockingQueueMock = mockk<BlockingQueue<DispatchableObject>>()

    private val sut = Dispatcher(
        blockingQueueMock,
        DirectExecutor(),
        LogLevel.None,
    ).apply {
        bot = botMock
    }

    private fun mockHandler(): Handler {
        return mockk {
            every { handleUpdate(any(), any()) } just runs
            every { checkUpdate(any()) } returns true
        }
    }

    private companion object {
        const val ANY_TEXT = "Valar Morghulis"
    }

    @Test
    fun `updates are dispatched to handlers when updates check starts and there are some updates`() {
        val mockHandler = mockHandler()
        sut.addHandler(mockHandler)
        val anyUpdate = anyUpdate()
        every { blockingQueueMock.take() } returns anyUpdate andThenThrows InterruptedException()

        try {
            sut.startCheckingUpdates()
        } catch (exception: InterruptedException) {
        } finally {
            verify(exactly = 1) { mockHandler.handleUpdate(botMock, anyUpdate) }
        }
    }

    @Test
    fun `handlers are not called after update is consumed`() {
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
        } catch (exception: InterruptedException) {
        } finally {
            assertTrue(anyMessageWithText.consumed)
            verify(exactly = 0) { handlerCallbackMock(any()) }
        }
    }

    @Test
    fun `test that handlers from different groups are called in consistent order`() {
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
        } catch (exception: InterruptedException) {
        } finally {
            verifyOrder {
                mockHandler1.handleUpdate(botMock, anyUpdate)
                mockHandler2.handleUpdate(botMock, anyUpdate)
                mockHandler3.handleUpdate(botMock, anyUpdate)
            }
        }
    }
}
