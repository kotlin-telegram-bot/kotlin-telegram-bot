package com.github.kotlintelegrambot.entities.inputmedia

import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MediaGroupTest {

    @Test
    fun `throws IllegalArgumentException when media group is created with zero medias`() {
        val exception = assertThrows<IllegalArgumentException> {
            MediaGroup.from()
        }

        assertEquals(MEDIA_GROUP_ILLEGAL_ARGUMENTS_SIZE_MESSAGE, exception.message)
    }

    @Test
    fun `throws IllegalArgumentException when media group is created with one media`() {
        val exception = assertThrows<IllegalArgumentException> {
            MediaGroup.from(anyInputMediaPhoto())
        }

        assertEquals(MEDIA_GROUP_ILLEGAL_ARGUMENTS_SIZE_MESSAGE, exception.message)
    }

    @Test
    fun `creates a MediaGroup when it's created with two medias`() {
        val anyInputMediaPhoto = anyInputMediaPhoto()
        val anyInputMediaVideo = anyInputMediaVideo()

        val mediaGroup = MediaGroup.from(anyInputMediaPhoto, anyInputMediaVideo)

        assertArrayEquals(arrayOf(anyInputMediaPhoto, anyInputMediaVideo), mediaGroup.medias)
    }

    @Test
    fun `creates a MediaGroup when it's created with ten medias`() {
        val anyInputMediaPhoto1 = anyInputMediaPhoto()
        val anyInputMediaPhoto2 = anyInputMediaPhoto()
        val anyInputMediaPhoto3 = anyInputMediaPhoto()
        val anyInputMediaPhoto4 = anyInputMediaPhoto()
        val anyInputMediaPhoto5 = anyInputMediaPhoto()
        val anyInputMediaVideo1 = anyInputMediaVideo()
        val anyInputMediaVideo2 = anyInputMediaVideo()
        val anyInputMediaVideo3 = anyInputMediaVideo()
        val anyInputMediaVideo4 = anyInputMediaVideo()
        val anyInputMediaVideo5 = anyInputMediaVideo()

        val mediaGroup = MediaGroup.from(
            anyInputMediaPhoto1,
            anyInputMediaPhoto2,
            anyInputMediaPhoto3,
            anyInputMediaPhoto4,
            anyInputMediaPhoto5,
            anyInputMediaVideo1,
            anyInputMediaVideo2,
            anyInputMediaVideo3,
            anyInputMediaVideo4,
            anyInputMediaVideo5
        )

        assertArrayEquals(
            arrayOf(
                anyInputMediaPhoto1,
                anyInputMediaPhoto2,
                anyInputMediaPhoto3,
                anyInputMediaPhoto4,
                anyInputMediaPhoto5,
                anyInputMediaVideo1,
                anyInputMediaVideo2,
                anyInputMediaVideo3,
                anyInputMediaVideo4,
                anyInputMediaVideo5
            ),
            mediaGroup.medias
        )
    }

    @Test
    fun `throws IllegalArgumentException when media group is created with eleven medias`() {
        val anyInputMediaPhoto1 = anyInputMediaPhoto()
        val anyInputMediaPhoto2 = anyInputMediaPhoto()
        val anyInputMediaPhoto3 = anyInputMediaPhoto()
        val anyInputMediaPhoto4 = anyInputMediaPhoto()
        val anyInputMediaPhoto5 = anyInputMediaPhoto()
        val anyInputMediaVideo1 = anyInputMediaVideo()
        val anyInputMediaVideo2 = anyInputMediaVideo()
        val anyInputMediaVideo3 = anyInputMediaVideo()
        val anyInputMediaVideo4 = anyInputMediaVideo()
        val anyInputMediaVideo5 = anyInputMediaVideo()
        val anyInputMediaVideo6 = anyInputMediaVideo()

        val exception = assertThrows<IllegalArgumentException> {
            MediaGroup.from(
                anyInputMediaPhoto1,
                anyInputMediaPhoto2,
                anyInputMediaPhoto3,
                anyInputMediaPhoto4,
                anyInputMediaPhoto5,
                anyInputMediaVideo1,
                anyInputMediaVideo2,
                anyInputMediaVideo3,
                anyInputMediaVideo4,
                anyInputMediaVideo5,
                anyInputMediaVideo6
            )
        }

        assertEquals(MEDIA_GROUP_ILLEGAL_ARGUMENTS_SIZE_MESSAGE, exception.message)
    }

    @Test
    fun `throws IllegalArgumentException when media group is created with document and some other type of media`() {
        val anyInputMediaPhoto = anyInputMediaPhoto()
        val anyInputMediaVideo = anyInputMediaVideo()
        val anyInputMediaDocument = anyInputMediaDocument()
        val anyInputMediaAudio = anyInputMediaAudio()
        val exceptions = listOf<IllegalArgumentException>(
            assertThrows {
                MediaGroup.from(anyInputMediaDocument, anyInputMediaPhoto)
            },
            assertThrows {
                MediaGroup.from(anyInputMediaDocument, anyInputMediaVideo)
            },
            assertThrows {
                MediaGroup.from(anyInputMediaDocument, anyInputMediaAudio)
            }
        )

        exceptions.forEach { assertEquals(MEDIA_GROUP_ILLEGAL_ARGUMENTS_COMBINATION_MESSAGE, it.message) }
    }

    @Test
    fun `throws IllegalArgumentException when media group is created with audio and and some other type of media`() {
        val anyInputMediaPhoto = anyInputMediaPhoto()
        val anyInputMediaVideo = anyInputMediaVideo()
        val anyInputMediaDocument = anyInputMediaDocument()
        val anyInputMediaAudio = anyInputMediaAudio()
        val exceptions = listOf<IllegalArgumentException>(
            assertThrows {
                MediaGroup.from(anyInputMediaAudio, anyInputMediaPhoto)
            },
            assertThrows {
                MediaGroup.from(anyInputMediaAudio, anyInputMediaVideo)
            },
            assertThrows {
                MediaGroup.from(anyInputMediaAudio, anyInputMediaDocument)
            }
        )

        exceptions.forEach { assertEquals(MEDIA_GROUP_ILLEGAL_ARGUMENTS_COMBINATION_MESSAGE, it.message) }
    }

    private companion object {
        const val MEDIA_GROUP_ILLEGAL_ARGUMENTS_SIZE_MESSAGE = "media groups must include 2-10 items"
        const val MEDIA_GROUP_ILLEGAL_ARGUMENTS_COMBINATION_MESSAGE =
            "Documents and audio files can be only grouped with messages of the same type"
    }
}
