package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BotDescriptionTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes BotDescription, BotShortDescription and BotName`() {
        assertThat(gson.fromJson("""{"description":"A bot"}""", BotDescription::class.java).description)
            .isEqualTo("A bot")
        assertThat(
            gson.fromJson("""{"short_description":"Short"}""", BotShortDescription::class.java)
                .shortDescription,
        ).isEqualTo("Short")
        assertThat(gson.fromJson("""{"name":"My Bot"}""", BotName::class.java).name)
            .isEqualTo("My Bot")
    }
}
