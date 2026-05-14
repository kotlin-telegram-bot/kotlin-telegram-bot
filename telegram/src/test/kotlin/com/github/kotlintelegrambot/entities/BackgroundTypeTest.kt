package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BackgroundTypeTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes BackgroundTypeFill by 'fill' discriminator`() {
        val json =
            """
            {"type":"fill","fill":{"type":"solid","color":255},"dark_theme_dimming":30}
            """.trimIndent()

        val background = gson.fromJson(json, BackgroundType::class.java)

        assertThat(background).isInstanceOf(BackgroundType.Fill::class.java)
        background as BackgroundType.Fill
        assertThat(background.darkThemeDimming).isEqualTo(30)
        assertThat(background.fill).isInstanceOf(BackgroundFill.Solid::class.java)
        assertThat((background.fill as BackgroundFill.Solid).color).isEqualTo(255L)
    }

    @Test
    fun `deserializes BackgroundTypeWallpaper by 'wallpaper' discriminator`() {
        val json =
            """
            {"type":"wallpaper","document":{"file_id":"abc","file_unique_id":"uniq"},"dark_theme_dimming":10,"is_blurred":true,"is_moving":false}
            """.trimIndent()

        val background = gson.fromJson(json, BackgroundType::class.java)

        assertThat(background).isInstanceOf(BackgroundType.Wallpaper::class.java)
        background as BackgroundType.Wallpaper
        assertThat(background.document.fileId).isEqualTo("abc")
        assertThat(background.document.fileUniqueId).isEqualTo("uniq")
        assertThat(background.darkThemeDimming).isEqualTo(10)
        assertThat(background.isBlurred).isTrue()
        assertThat(background.isMoving).isFalse()
    }

    @Test
    fun `deserializes BackgroundTypePattern by 'pattern' discriminator`() {
        val json =
            """
            {"type":"pattern","document":{"file_id":"pid","file_unique_id":"puniq"},"fill":{"type":"gradient","top_color":1,"bottom_color":2,"rotation_angle":90},"intensity":50,"is_inverted":true}
            """.trimIndent()

        val background = gson.fromJson(json, BackgroundType::class.java)

        assertThat(background).isInstanceOf(BackgroundType.Pattern::class.java)
        background as BackgroundType.Pattern
        assertThat(background.document.fileId).isEqualTo("pid")
        assertThat(background.intensity).isEqualTo(50)
        assertThat(background.isInverted).isTrue()
        assertThat(background.fill).isInstanceOf(BackgroundFill.Gradient::class.java)
    }

    @Test
    fun `deserializes BackgroundTypeChatTheme by 'chat_theme' discriminator`() {
        val json = """{"type":"chat_theme","theme_name":"Sunrise"}"""

        val background = gson.fromJson(json, BackgroundType::class.java)

        assertThat(background).isInstanceOf(BackgroundType.ChatTheme::class.java)
        background as BackgroundType.ChatTheme
        assertThat(background.themeName).isEqualTo("Sunrise")
    }
}
