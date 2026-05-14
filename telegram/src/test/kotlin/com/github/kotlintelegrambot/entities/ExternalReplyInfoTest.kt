package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExternalReplyInfoTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes ExternalReplyInfo with minimal origin`() {
        val json = """
            {
              "origin":{"type":"user","date":1700000000,"sender_user":{"id":1,"is_bot":false,"first_name":"Alice"}},
              "chat":{"id":-1,"type":"channel"},
              "message_id":5,
              "link_preview_options":{"is_disabled":true},
              "has_media_spoiler":false
            }
        """.trimIndent()

        val info = gson.fromJson(json, ExternalReplyInfo::class.java)

        assertThat(info.origin).isInstanceOf(MessageOrigin.User::class.java)
        assertThat(info.chat?.id).isEqualTo(-1L)
        assertThat(info.messageId).isEqualTo(5L)
        assertThat(info.linkPreviewOptions?.isDisabled).isTrue()
        assertThat(info.hasMediaSpoiler).isFalse()
        assertThat(info.animation).isNull()
        assertThat(info.audio).isNull()
        assertThat(info.document).isNull()
        assertThat(info.photo).isNull()
        assertThat(info.sticker).isNull()
        assertThat(info.story).isNull()
        assertThat(info.video).isNull()
        assertThat(info.videoNote).isNull()
        assertThat(info.voice).isNull()
        assertThat(info.contact).isNull()
        assertThat(info.dice).isNull()
        assertThat(info.game).isNull()
        assertThat(info.giveaway).isNull()
        assertThat(info.giveawayWinners).isNull()
        assertThat(info.invoice).isNull()
        assertThat(info.location).isNull()
        assertThat(info.poll).isNull()
        assertThat(info.venue).isNull()
    }

    @Test
    fun `deserializes ExternalReplyInfo with story and photo`() {
        val json = """
            {
              "origin":{"type":"hidden_user","date":1700000000,"sender_user_name":"Hidden"},
              "story":{"chat":{"id":-7,"type":"channel"},"id":3},
              "photo":[{"file_id":"f1","file_unique_id":"u1","width":100,"height":100}]
            }
        """.trimIndent()

        val info = gson.fromJson(json, ExternalReplyInfo::class.java)

        assertThat(info.origin).isInstanceOf(MessageOrigin.HiddenUser::class.java)
        assertThat(info.story?.id).isEqualTo(3)
        assertThat(info.story?.chat?.id).isEqualTo(-7L)
        assertThat(info.photo).hasSize(1)
        assertThat(info.photo?.get(0)?.fileId).isEqualTo("f1")
    }
}
