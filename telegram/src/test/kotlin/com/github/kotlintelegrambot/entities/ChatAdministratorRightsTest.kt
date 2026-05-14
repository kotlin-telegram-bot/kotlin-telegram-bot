package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatAdministratorRightsTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes minimal payload with only mandatory boolean rights`() {
        val json = """
            {
              "is_anonymous": false,
              "can_manage_chat": true,
              "can_delete_messages": true,
              "can_manage_video_chats": false,
              "can_restrict_members": true,
              "can_promote_members": false,
              "can_change_info": true,
              "can_invite_users": true
            }
        """.trimIndent()

        val rights = gson.fromJson(json, ChatAdministratorRights::class.java)

        assertThat(rights.isAnonymous).isFalse()
        assertThat(rights.canManageChat).isTrue()
        assertThat(rights.canDeleteMessages).isTrue()
        assertThat(rights.canManageVideoChats).isFalse()
        assertThat(rights.canRestrictMembers).isTrue()
        assertThat(rights.canPromoteMembers).isFalse()
        assertThat(rights.canChangeInfo).isTrue()
        assertThat(rights.canInviteUsers).isTrue()
        assertThat(rights.canPostMessages).isNull()
        assertThat(rights.canManageTopics).isNull()
        assertThat(rights.canPostStories).isNull()
    }

    @Test
    fun `deserializes channel + supergroup + stories rights`() {
        val json = """
            {
              "is_anonymous": true,
              "can_manage_chat": true,
              "can_delete_messages": true,
              "can_manage_video_chats": true,
              "can_restrict_members": true,
              "can_promote_members": true,
              "can_change_info": true,
              "can_invite_users": true,
              "can_post_messages": true,
              "can_edit_messages": true,
              "can_pin_messages": false,
              "can_manage_topics": true,
              "can_post_stories": true,
              "can_edit_stories": true,
              "can_delete_stories": false
            }
        """.trimIndent()

        val rights = gson.fromJson(json, ChatAdministratorRights::class.java)

        assertThat(rights.canPostMessages).isTrue()
        assertThat(rights.canManageTopics).isTrue()
        assertThat(rights.canPostStories).isTrue()
        assertThat(rights.canEditStories).isTrue()
        assertThat(rights.canDeleteStories).isFalse()
    }
}
