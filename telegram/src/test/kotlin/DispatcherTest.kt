import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.handlers.HandleText
import com.github.kotlintelegrambot.dispatcher.handlers.HandleUpdate
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandler
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.DispatchableObject
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.BlockingQueue

class DispatcherTest {

    private val botMock = mockk<Bot>()
    private val blockingQueueMock = mockk<BlockingQueue<DispatchableObject>>()

    private fun dispatcher() = Dispatcher(blockingQueueMock).apply {
        bot = botMock
        logLevel = LogLevel.None
    }

    private fun mockHandler(
        handlerCallbackMock: HandleUpdate,
        groupIdentifierMock: String = ""
    ): Handler {
        return mockk {
            every { handlerCallback } returns handlerCallbackMock
            every { checkUpdate(any()) } returns true
            every { groupIdentifier } returns groupIdentifierMock
        }
    }

    private companion object {
        const val ANY_TEXT = "Valar Morghulis"
    }

    @Test
    fun `updates are dispatched to handlers when updates check starts and there are some updates`() {
        val sut = dispatcher()

        val handlerCallbackMock = mockk<HandleUpdate>(relaxed = true)
        sut.addHandler(mockHandler(handlerCallbackMock))
        val anyUpdate = anyUpdate()
        every { blockingQueueMock.take() } returns anyUpdate andThenThrows InterruptedException()

        try {
            sut.startCheckingUpdates()
        } catch (exception: InterruptedException) {
        } finally {
            verify(exactly = 1) { handlerCallbackMock(botMock, anyUpdate) }
        }
    }

    @Test
    fun `handlers are not called after update is consumed`() {
        val sut = dispatcher()
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
        val sut = dispatcher()
        val firstHandlerCallbackMock = mockk<HandleUpdate>(relaxed = true)
        val secondHandlerCallbackMock = mockk<HandleUpdate>(relaxed = true)
        val thirdHandlerCallbackMock = mockk<HandleUpdate>(relaxed = true)
        sut.addHandler(mockHandler(firstHandlerCallbackMock, "Group 1"))
        sut.addHandler(mockHandler(secondHandlerCallbackMock, "Group 2"))
        sut.addHandler(mockHandler(thirdHandlerCallbackMock, "Group 3"))

        val anyUpdate = anyUpdate()
        every { blockingQueueMock.take() } returns anyUpdate andThenThrows InterruptedException()

        try {
            sut.startCheckingUpdates()
        } catch (exception: InterruptedException) {
        } finally {
            verifyOrder {
                firstHandlerCallbackMock(botMock, anyUpdate)
                secondHandlerCallbackMock(botMock, anyUpdate)
                thirdHandlerCallbackMock(botMock, anyUpdate)
            }
        }
    }
}
