package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.stories.InputStoryContent
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for Bot API 9.0 story methods.
 */
class StoriesIT : ApiClientIT() {
    @Test
    fun `postStory posts business_connection_id, serialized content and active_period`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"ok":true,"result":{"chat":{"id":1,"type":"private"},"id":7}}""",
            ),
        )

        sut.postStory(
            businessConnectionId = "conn-abc",
            content = InputStoryContent.Photo(photo = "file-id"),
            activePeriod = 86400,
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/postStory"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("content="))
        assertEquals(true, body.contains("\"type\":\"photo\""))
        assertEquals(true, body.contains("\"photo\":\"file-id\""))
        assertEquals(true, body.contains("active_period=86400"))
    }

    @Test
    fun `editStory posts business_connection_id, story_id and serialized content`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"ok":true,"result":{"chat":{"id":1,"type":"private"},"id":7}}""",
            ),
        )

        sut.editStory(
            businessConnectionId = "conn-abc",
            storyId = 7,
            content = InputStoryContent.Photo(photo = "file-id-2"),
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/editStory"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("story_id=7"))
        assertEquals(true, body.contains("content="))
        assertEquals(true, body.contains("\"photo\":\"file-id-2\""))
    }

    @Test
    fun `deleteStory posts business_connection_id and story_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.deleteStory(businessConnectionId = "conn-abc", storyId = 7)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/deleteStory"))
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("story_id=7"))
    }
}
