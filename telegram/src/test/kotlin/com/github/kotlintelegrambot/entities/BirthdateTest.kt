package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BirthdateTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes Birthdate with year`() {
        val json = """{"day":14,"month":5,"year":1990}"""

        val birthdate = gson.fromJson(json, Birthdate::class.java)

        assertThat(birthdate.day).isEqualTo(14)
        assertThat(birthdate.month).isEqualTo(5)
        assertThat(birthdate.year).isEqualTo(1990)
    }

    @Test
    fun `deserializes Birthdate without year`() {
        val json = """{"day":1,"month":12}"""

        val birthdate = gson.fromJson(json, Birthdate::class.java)

        assertThat(birthdate.day).isEqualTo(1)
        assertThat(birthdate.month).isEqualTo(12)
        assertThat(birthdate.year).isNull()
    }
}
