package com.github.kotlintelegrambot.entities

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class LocationTest {
    @Test
    fun `should permit to not inform livePeriod optional attribute value`() {
        val location = Location(2.2F, 2.5F)
        Assertions.assertEquals(location.livePeriod, null)
    }

    @Test
    fun `should permit to inform livePeriod optional attribute value`() {
        val livePeriod = 23
        val location = Location(2.2F, 2.5F, livePeriod)
        Assertions.assertEquals(location.livePeriod, livePeriod)
    }
}
