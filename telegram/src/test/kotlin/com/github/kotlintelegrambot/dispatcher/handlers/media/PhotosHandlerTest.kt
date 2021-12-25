package com.github.kotlintelegrambot.dispatcher.handlers.media

import anyMessage
import anyPhotoSize
import anyUpdate
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.HandlePhotos
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhotosHandlerTest {

    private val handlePhotosMock = mockk<HandlePhotos>(relaxed = true)

    private val sut = PhotosHandler(handlePhotosMock)

    @Test
    fun `checkUpdate returns false when there are no photos`() {
        val anyUpdateWithNoPhotos = anyUpdate(message = anyMessage(photo = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoPhotos)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when there are empty photos`() {
        val anyUpdateWithEmptyPhotos = anyUpdate(message = anyMessage(photo = emptyList()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithEmptyPhotos)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there are photos`() {
        val anyUpdateWithPhotos = anyUpdate(message = anyMessage(photo = listOf(anyPhotoSize())))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithPhotos)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `photos are properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyPhotos = listOf(anyPhotoSize())
        val anyMessageWithPhotos = anyMessage(photo = anyPhotos)
        val anyUpdateWithPhotos = anyUpdate(message = anyMessage(photo = anyPhotos))

        sut.handleUpdate(botMock, anyUpdateWithPhotos)

        val expectedPhotoHandlerEnv = MediaHandlerEnvironment(
            botMock,
            anyUpdateWithPhotos,
            anyMessageWithPhotos,
            anyPhotos
        )
        coVerify { handlePhotosMock.invoke(expectedPhotoHandlerEnv) }
    }
}
