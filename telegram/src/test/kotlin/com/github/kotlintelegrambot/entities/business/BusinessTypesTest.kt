package com.github.kotlintelegrambot.entities.business

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BusinessTypesTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes BusinessConnection with legacy can_reply field`() {
        val json = """
            {"id":"abc","user":{"id":42,"is_bot":false,"first_name":"Alice"},
             "user_chat_id":99,"date":1700000000,"can_reply":true,"is_enabled":true}
        """.trimIndent()

        val bc = gson.fromJson(json, BusinessConnection::class.java)

        assertThat(bc.id).isEqualTo("abc")
        assertThat(bc.user.firstName).isEqualTo("Alice")
        assertThat(bc.userChatId).isEqualTo(99L)
        assertThat(bc.canReply).isTrue()
        assertThat(bc.rights).isNull()
        assertThat(bc.isEnabled).isTrue()
    }

    @Test
    fun `deserializes BusinessConnection with rights (Bot API 9_0)`() {
        val json = """
            {"id":"abc","user":{"id":42,"is_bot":false,"first_name":"Alice"},
             "user_chat_id":99,"date":1700000000,"is_enabled":true,
             "rights":{"can_reply":true,"can_read_messages":true,"can_edit_bio":false}}
        """.trimIndent()

        val bc = gson.fromJson(json, BusinessConnection::class.java)

        assertThat(bc.rights).isNotNull
        assertThat(bc.rights!!.canReply).isTrue()
        assertThat(bc.rights!!.canReadMessages).isTrue()
        assertThat(bc.rights!!.canEditBio).isFalse()
    }

    @Test
    fun `deserializes BusinessMessagesDeleted`() {
        val json = """
            {"business_connection_id":"abc","chat":{"id":1,"type":"private"},"message_ids":[1,2,3]}
        """.trimIndent()

        val d = gson.fromJson(json, BusinessMessagesDeleted::class.java)

        assertThat(d.businessConnectionId).isEqualTo("abc")
        assertThat(d.chat.id).isEqualTo(1L)
        assertThat(d.messageIds).containsExactly(1L, 2L, 3L)
    }

    @Test
    fun `deserializes BusinessIntro, BusinessLocation, BusinessOpeningHours`() {
        val intro = gson.fromJson("""{"title":"Hi","message":"Welcome"}""", BusinessIntro::class.java)
        assertThat(intro.title).isEqualTo("Hi")
        assertThat(intro.message).isEqualTo("Welcome")
        assertThat(intro.sticker).isNull()

        val loc = gson.fromJson(
            """{"address":"42 Main St","location":{"latitude":1.0,"longitude":2.0}}""",
            BusinessLocation::class.java,
        )
        assertThat(loc.address).isEqualTo("42 Main St")
        assertThat(loc.location?.latitude).isEqualTo(1.0f)

        val hours = gson.fromJson(
            """{"time_zone_name":"Europe/Madrid","opening_hours":[{"opening_minute":540,"closing_minute":1020}]}""",
            BusinessOpeningHours::class.java,
        )
        assertThat(hours.timeZoneName).isEqualTo("Europe/Madrid")
        assertThat(hours.openingHours).hasSize(1)
        assertThat(hours.openingHours[0].openingMinute).isEqualTo(540)
        assertThat(hours.openingHours[0].closingMinute).isEqualTo(1020)
    }
}
