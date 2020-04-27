import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.concurrent.BlockingQueue
import me.ivmg.telegram.Bot
import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.dispatcher.Dispatcher
import me.ivmg.telegram.dispatcher.handlers.Handler
import me.ivmg.telegram.types.DispatchableObject
import org.junit.jupiter.api.Test

class DispatcherTest {

    private val botMock = mockk<Bot>()
    private val blockingQueueMock = mockk<BlockingQueue<DispatchableObject>>()

    private val sut = Dispatcher(updatesQueue = blockingQueueMock).apply {
        bot = botMock
    }

    @Test
    fun `updates are dispatched to handlers when updates check starts and there are some updates`() {
        val handlerCallbackMock = mockk<HandleUpdate>(relaxed = true)
        val handlerMock = mockk<Handler> {
            every { handlerCallback } returns handlerCallbackMock
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
            verify(exactly = 1) { handlerCallbackMock(botMock, anyUpdate) }
        }
    }
}
