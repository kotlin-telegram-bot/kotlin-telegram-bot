package com.github.kotlintelegrambot.entities

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class LocationTest {
    @Test
    fun `should permit to not inform heading optional attribute value`() {
        val location = Location(23.4F, 45.0F)
        Assertions.assertEquals(location.heading, null)
    }

    @Test
    fun `should permit to inform heading optional attribute value`() {
        val locationHeading = 34
        val location = Location(23.4F, 45.0F, locationHeading)
        Assertions.assertEquals(location.heading, locationHeading)
    }
}
