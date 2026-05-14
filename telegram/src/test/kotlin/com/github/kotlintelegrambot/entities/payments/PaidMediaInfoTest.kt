package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PaidMediaInfoTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes PaidMediaInfo with star count and paid media list`() {
        val json = """
            {"star_count":99,
             "paid_media":[
               {"type":"preview","width":100,"height":200,"duration":10},
               {"type":"photo","photo":[{"file_id":"A","file_unique_id":"u","width":1,"height":1}]}
             ]}
        """.trimIndent()

        val info = gson.fromJson(json, PaidMediaInfo::class.java)

        assertThat(info.starCount).isEqualTo(99)
        assertThat(info.paidMedia).hasSize(2)
        assertThat(info.paidMedia[0]).isInstanceOf(PaidMedia.Preview::class.java)
        assertThat(info.paidMedia[1]).isInstanceOf(PaidMedia.Photo::class.java)
    }
}
