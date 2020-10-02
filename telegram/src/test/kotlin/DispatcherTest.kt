
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.DispatchableObject
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.concurrent.BlockingQueue

class DispatcherTest {

    private val botMock = mockk<Bot>()
    private val blockingQueueMock = mockk<BlockingQueue<DispatchableObject>>()

    private val sut = Dispatcher(blockingQueueMock).apply {
        bot = botMock
        logLevel = LogLevel.None
    }

    @Test
    fun `updates are dispatched to handlers when updates check starts and there are some updates`() {
        val handlerMock = mockk<Handler> {
            every { checkUpdate(any()) } returns true
            every { groupIdentifier } returns ""
        }
        sut.addHandler(handlerMock)
        val anyUpdate = anyUpdate()
        every { blockingQueueMock.take() } returns anyUpdate andThenThrows InterruptedException()

        try {
            sut.startCheckingUpdates()
        } catch (exception: InterruptedException) {
        } finally {
            verify(exactly = 1) { handlerMock(botMock, anyUpdate) }
        }
    }
}
