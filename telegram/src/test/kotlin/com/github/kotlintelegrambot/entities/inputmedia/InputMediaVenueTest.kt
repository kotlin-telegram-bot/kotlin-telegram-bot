package com.github.kotlintelegrambot.entities.inputmedia

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputMediaVenueTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes InputMediaVenue with all fields`() {
        val json = """
            {"type":"venue","latitude":41.39,"longitude":2.16,"title":"Sagrada","address":"Carrer",
             "foursquare_id":"fid","foursquare_type":"ft",
             "google_place_id":"gpid","google_place_type":"gpt"}
        """.trimIndent()

        val venue = gson.fromJson(json, InputMediaVenue::class.java)

        assertThat(venue.type).isEqualTo("venue")
        assertThat(venue.title).isEqualTo("Sagrada")
        assertThat(venue.address).isEqualTo("Carrer")
        assertThat(venue.foursquareId).isEqualTo("fid")
        assertThat(venue.foursquareType).isEqualTo("ft")
        assertThat(venue.googlePlaceId).isEqualTo("gpid")
        assertThat(venue.googlePlaceType).isEqualTo("gpt")
    }

    @Test
    fun `deserializes minimal InputMediaVenue`() {
        val json = """
            {"type":"venue","latitude":0.0,"longitude":0.0,"title":"T","address":"A"}
        """.trimIndent()

        val venue = gson.fromJson(json, InputMediaVenue::class.java)

        assertThat(venue.foursquareId).isNull()
        assertThat(venue.foursquareType).isNull()
        assertThat(venue.googlePlaceId).isNull()
        assertThat(venue.googlePlaceType).isNull()
    }
}
