package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BackgroundFillTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes BackgroundFillSolid by 'solid' discriminator`() {
        val json = """{"type":"solid","color":16777215}"""

        val fill = gson.fromJson(json, BackgroundFill::class.java)

        assertThat(fill).isInstanceOf(BackgroundFill.Solid::class.java)
        fill as BackgroundFill.Solid
        assertThat(fill.color).isEqualTo(16777215L)
    }

    @Test
    fun `deserializes BackgroundFillGradient by 'gradient' discriminator`() {
        val json = """
            {"type":"gradient","top_color":16711680,"bottom_color":255,"rotation_angle":45}
        """.trimIndent()

        val fill = gson.fromJson(json, BackgroundFill::class.java)

        assertThat(fill).isInstanceOf(BackgroundFill.Gradient::class.java)
        fill as BackgroundFill.Gradient
        assertThat(fill.topColor).isEqualTo(16711680L)
        assertThat(fill.bottomColor).isEqualTo(255L)
        assertThat(fill.rotationAngle).isEqualTo(45)
    }

    @Test
    fun `deserializes BackgroundFillFreeformGradient by 'freeform_gradient' discriminator`() {
        val json = """{"type":"freeform_gradient","colors":[1,2,3,4]}"""

        val fill = gson.fromJson(json, BackgroundFill::class.java)

        assertThat(fill).isInstanceOf(BackgroundFill.FreeformGradient::class.java)
        fill as BackgroundFill.FreeformGradient
        assertThat(fill.colors).containsExactly(1L, 2L, 3L, 4L)
    }
}
