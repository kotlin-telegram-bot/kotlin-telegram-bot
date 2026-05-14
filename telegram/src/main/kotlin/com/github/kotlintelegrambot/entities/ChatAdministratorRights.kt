package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

/**
 * Represents the rights of an administrator in a chat.
 *
 * https://core.telegram.org/bots/api#chatadministratorrights
 */
data class ChatAdministratorRights(
    @Name("is_anonymous") val isAnonymous: Boolean,
    @Name("can_manage_chat") val canManageChat: Boolean,
    @Name("can_delete_messages") val canDeleteMessages: Boolean,
    @Name("can_manage_video_chats") val canManageVideoChats: Boolean,
    @Name("can_restrict_members") val canRestrictMembers: Boolean,
    @Name("can_promote_members") val canPromoteMembers: Boolean,
    @Name("can_change_info") val canChangeInfo: Boolean,
    @Name("can_invite_users") val canInviteUsers: Boolean,
    @Name("can_post_messages") val canPostMessages: Boolean? = null,
    @Name("can_edit_messages") val canEditMessages: Boolean? = null,
    @Name("can_pin_messages") val canPinMessages: Boolean? = null,
    @Name("can_manage_topics") val canManageTopics: Boolean? = null,
    @Name("can_post_stories") val canPostStories: Boolean? = null,
    @Name("can_edit_stories") val canEditStories: Boolean? = null,
    @Name("can_delete_stories") val canDeleteStories: Boolean? = null,
)
