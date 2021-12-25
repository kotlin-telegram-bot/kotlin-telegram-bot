package com.github.kotlintelegrambot.dispatcher.handlers.media

import anyAnimation
import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.HandleAnimation
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnimationHandlerTest {

    private val handleAnimationMock = mockk<HandleAnimation>(relaxed = true)

    private val sut = AnimationHandler(handleAnimationMock)

    @Test
    fun `checkUpdate returns false when there is no animation`() {
        val anyUpdateWithNoAnimation = anyUpdate(message = anyMessage(animation = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoAnimation)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is animation`() {
        val anyUpdateWithAnimation = anyUpdate(message = anyMessage(animation = anyAnimation()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithAnimation)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `animation is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyAnimation = anyAnimation()
        val anyMessageWithAnimation = anyMessage(animation = anyAnimation)
        val anyUpdateWithAnimation = anyUpdate(message = anyMessageWithAnimation)

        sut.handleUpdate(botMock, anyUpdateWithAnimation)

        val expectedAnimationHandlerEnv = MediaHandlerEnvironment(
            botMock,
            anyUpdateWithAnimation,
            anyMessageWithAnimation,
            anyAnimation
        )
        coVerify { handleAnimationMock.invoke(expectedAnimationHandlerEnv) }
    }
}
