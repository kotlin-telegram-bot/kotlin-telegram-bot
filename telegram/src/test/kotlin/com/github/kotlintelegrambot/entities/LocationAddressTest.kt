package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocationAddressTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes LocationAddress with all fields`() {
        val json = """{"country_code":"US","state":"CA","city":"San Francisco","street":"Market St"}"""

        val address = gson.fromJson(json, LocationAddress::class.java)

        assertThat(address.countryCode).isEqualTo("US")
        assertThat(address.state).isEqualTo("CA")
        assertThat(address.city).isEqualTo("San Francisco")
        assertThat(address.street).isEqualTo("Market St")
    }

    @Test
    fun `deserializes LocationAddress with only required fields`() {
        val json = """{"country_code":"ES"}"""

        val address = gson.fromJson(json, LocationAddress::class.java)

        assertThat(address.countryCode).isEqualTo("ES")
        assertThat(address.state).isNull()
        assertThat(address.city).isNull()
        assertThat(address.street).isNull()
    }
}
