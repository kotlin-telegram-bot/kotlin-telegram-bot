package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserRatingTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes the four-field shape`() {
        val json = """{"level":3,"rating":12500,"current_level_rating":10000,"next_level_rating":20000}"""

        val rating = gson.fromJson(json, UserRating::class.java)

        assertThat(rating.level).isEqualTo(3)
        assertThat(rating.rating).isEqualTo(12500)
        assertThat(rating.currentLevelRating).isEqualTo(10000)
        assertThat(rating.nextLevelRating).isEqualTo(20000)
    }

    @Test
    fun `next_level_rating is optional (max-level user)`() {
        val json = """{"level":99,"rating":999999,"current_level_rating":900000}"""

        val rating = gson.fromJson(json, UserRating::class.java)

        assertThat(rating.nextLevelRating).isNull()
    }
}
