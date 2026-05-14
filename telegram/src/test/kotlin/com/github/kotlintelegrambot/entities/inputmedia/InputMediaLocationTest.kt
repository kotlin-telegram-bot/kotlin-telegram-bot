package com.github.kotlintelegrambot.entities.inputmedia

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test

class InputMediaLocationTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes InputMediaLocation with all fields`() {
        val json = """
            {"type":"location","latitude":41.39,"longitude":2.16,
             "horizontal_accuracy":12.5,"live_period":600,"heading":90,"proximity_alert_radius":500}
        """.trimIndent()

        val loc = gson.fromJson(json, InputMediaLocation::class.java)

        assertThat(loc.type).isEqualTo("location")
        assertThat(loc.latitude).isEqualTo(41.39f, Offset.offset(0.001f))
        assertThat(loc.longitude).isEqualTo(2.16f, Offset.offset(0.001f))
        assertThat(loc.horizontalAccuracy).isEqualTo(12.5f)
        assertThat(loc.livePeriod).isEqualTo(600)
        assertThat(loc.heading).isEqualTo(90)
        assertThat(loc.proximityAlertRadius).isEqualTo(500)
    }

    @Test
    fun `deserializes minimal InputMediaLocation`() {
        val json = """{"type":"location","latitude":0.0,"longitude":0.0}"""

        val loc = gson.fromJson(json, InputMediaLocation::class.java)

        assertThat(loc.horizontalAccuracy).isNull()
        assertThat(loc.livePeriod).isNull()
        assertThat(loc.heading).isNull()
        assertThat(loc.proximityAlertRadius).isNull()
    }
}
