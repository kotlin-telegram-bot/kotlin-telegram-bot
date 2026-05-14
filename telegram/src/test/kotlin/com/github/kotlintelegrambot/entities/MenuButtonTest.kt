package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MenuButtonTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes MenuButtonCommands`() {
        val json = """{"type":"commands"}"""

        val button = gson.fromJson(json, MenuButton.Commands::class.java)

        assertThat(button.type).isEqualTo("commands")
    }

    @Test
    fun `deserializes MenuButtonWebApp with text and web_app url`() {
        val json = """{"type":"web_app","text":"Launch","web_app":{"url":"https://example.com/app"}}"""

        val button = gson.fromJson(json, MenuButton.WebApp::class.java)

        assertThat(button.type).isEqualTo("web_app")
        assertThat(button.text).isEqualTo("Launch")
        assertThat(button.webApp.url).isEqualTo("https://example.com/app")
    }

    @Test
    fun `deserializes MenuButtonDefault`() {
        val json = """{"type":"default"}"""

        val button = gson.fromJson(json, MenuButton.Default::class.java)

        assertThat(button.type).isEqualTo("default")
    }

    @Test
    fun `MenuButton adapter dispatches by 'type' discriminator`() {
        val commands = gson.fromJson("""{"type":"commands"}""", MenuButton::class.java)
        val webApp =
            gson.fromJson(
                """{"type":"web_app","text":"Launch","web_app":{"url":"https://example.com/app"}}""",
                MenuButton::class.java,
            )
        val default = gson.fromJson("""{"type":"default"}""", MenuButton::class.java)

        assertThat(commands).isInstanceOf(MenuButton.Commands::class.java)
        assertThat(webApp).isInstanceOf(MenuButton.WebApp::class.java)
        assertThat((webApp as MenuButton.WebApp).webApp.url).isEqualTo("https://example.com/app")
        assertThat(default).isInstanceOf(MenuButton.Default::class.java)
    }

    @Test
    fun `serializes MenuButtonWebApp using snake_case fields`() {
        val button =
            MenuButton.WebApp(
                text = "Open",
                webApp =
                    com.github.kotlintelegrambot.entities.keyboard
                        .WebAppInfo("https://example.com/app"),
            )

        val json = gson.toJson(button)

        assertThat(json).contains("\"type\":\"web_app\"")
        assertThat(json).contains("\"text\":\"Open\"")
        assertThat(json).contains("\"web_app\":{\"url\":\"https://example.com/app\"}")
    }
}
