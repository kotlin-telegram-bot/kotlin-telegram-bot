package com.github.kotlintelegrambot.entities.stories

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StoryAreaTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes StoryAreaPosition`() {
        val json = """
            {
              "x_percentage":50.0,
              "y_percentage":25.0,
              "width_percentage":10.0,
              "height_percentage":5.0,
              "rotation_angle":0.0,
              "corner_radius_percentage":12.5
            }
        """.trimIndent()

        val position = gson.fromJson(json, StoryAreaPosition::class.java)

        assertThat(position.xPercentage).isEqualTo(50.0f)
        assertThat(position.yPercentage).isEqualTo(25.0f)
        assertThat(position.widthPercentage).isEqualTo(10.0f)
        assertThat(position.heightPercentage).isEqualTo(5.0f)
        assertThat(position.rotationAngle).isEqualTo(0.0f)
        assertThat(position.cornerRadiusPercentage).isEqualTo(12.5f)
    }
}
