package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LinkPreviewOptionsTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `serializes is_disabled with snake_case key`() {
        val options = LinkPreviewOptions(isDisabled = true)

        val json = gson.toJson(options)

        assertThat(json).isEqualTo("""{"is_disabled":true}""")
    }

    @Test
    fun `serializes every field with snake_case keys`() {
        val options = LinkPreviewOptions(
            isDisabled = false,
            url = "https://example.com",
            preferSmallMedia = true,
            preferLargeMedia = false,
            showAboveText = true,
        )

        val json = gson.toJson(options)

        assertThat(json).contains("\"is_disabled\":false")
        assertThat(json).contains("\"url\":\"https://example.com\"")
        assertThat(json).contains("\"prefer_small_media\":true")
        assertThat(json).contains("\"prefer_large_media\":false")
        assertThat(json).contains("\"show_above_text\":true")
    }

    @Test
    fun `deserializes from server-shaped JSON`() {
        val json = """{"is_disabled":false,"url":"https://example.com","show_above_text":true}"""

        val options = gson.fromJson(json, LinkPreviewOptions::class.java)

        assertThat(options.isDisabled).isFalse()
        assertThat(options.url).isEqualTo("https://example.com")
        assertThat(options.showAboveText).isTrue()
        assertThat(options.preferSmallMedia).isNull()
        assertThat(options.preferLargeMedia).isNull()
    }
}
