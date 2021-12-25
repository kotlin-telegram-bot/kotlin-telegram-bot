package com.github.kotlintelegrambot.dispatcher.handlers

import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommandHandlerTest {

    private val handlerFunctionMock = mockk<suspend CommandHandlerEnvironment.() -> Unit>(relaxed = true)

    private val sut = CommandHandler(ANY_COMMAND_NAME, handlerFunctionMock)

    @Test
    fun `checkUpdate returns true for the given command`() {
        val anyCommand = anyUpdate(message = anyMessage(text = "/$ANY_COMMAND_NAME"))

        val checkUpdateResult = sut.checkUpdate(anyCommand)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true for the given command with arguments`() {
        val anyCommandWithArguments = anyUpdate(
            message = anyMessage(text = "/$ANY_COMMAND_NAME a b")
        )

        val checkUpdateResult = sut.checkUpdate(anyCommandWithArguments)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false for another command`() {
        val anyOtherCommand = anyUpdate(message = anyMessage(text = "/fake"))

        val checkUpdateResult = sut.checkUpdate(anyOtherCommand)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false for a non command`() {
        val anyNonCommandUpdate = anyUpdate(message = anyMessage(text = "non command text"))

        val checkUpdateResult = sut.checkUpdate(anyNonCommandUpdate)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `command update is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val commandMessage = anyMessage(text = "/$ANY_COMMAND_NAME $ANY_ARG")
        val anyUpdate = anyUpdate(message = commandMessage)

        sut.handleUpdate(botMock, anyUpdate)

        val expectedArgs = CommandHandlerEnvironment(botMock, anyUpdate, commandMessage, listOf(ANY_ARG))
        coVerify { handlerFunctionMock.invoke(expectedArgs) }
    }

    private companion object {
        const val ANY_COMMAND_NAME = "test"
        const val ANY_ARG = "arggggg"
    }
}
