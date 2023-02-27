package com.github.kotlintelegrambot.dispatcher.handlers

import anyPollAnswer
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class PollAnswerHandlerTest {

    private val handlePollAnswerMock = mockk<HandlePollAnswer>(relaxed = true)

    private val sut = PollAnswerHandler(handlePollAnswerMock)

    @Test
    fun `checkUpdate returns false when there is no poll answer`() {
        val anyUpdateWithNoPollAnswer = anyUpdate(pollAnswer = null)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoPollAnswer)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is poll answer`() {
        val anyUpdateWithPollAnswer = anyUpdate(pollAnswer = anyPollAnswer())

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithPollAnswer)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `poll answer is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyPollAnswer = anyPollAnswer()
        val anyUpdateWithPollAnswer = anyUpdate(pollAnswer = anyPollAnswer)

        sut.handleUpdate(botMock, anyUpdateWithPollAnswer)

        val expectedPollAnswerHandlerEnv = PollAnswerHandlerEnvironment(
            botMock,
            anyUpdateWithPollAnswer,
            anyPollAnswer
        )
        coVerify { handlePollAnswerMock.invoke(expectedPollAnswerHandlerEnv) }
    }
}
