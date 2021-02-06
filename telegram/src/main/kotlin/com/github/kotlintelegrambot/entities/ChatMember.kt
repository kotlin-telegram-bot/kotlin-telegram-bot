package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

data class ChatMember(
    @SerializedName("user") val user: User,
    @SerializedName("status") val status: String,
    @SerializedName("custom_title") val customTitle: String? = null,
    @SerializedName("is_anonymous") val isAnonymous: Boolean? = null,
    @SerializedName("until_date") val forceReply: Int? = null,
    @SerializedName("can_be_edited") val canBeEdited: Boolean? = null,
    @SerializedName("can_post_messages") val canPostMessages: Boolean? = null,
    @SerializedName("can_edit_messages") val canEditMessages: Boolean? = null,
    @SerializedName("can_delete_messages") val canDeleteMessages: Boolean? = null,
    @SerializedName("can_restrict_members") val canRestrictMembers: Boolean? = null,
    @SerializedName("can_promote_members") val canPromoteMembers: Boolean? = null,
    @SerializedName("can_change_info") val canChangeInfo: Boolean? = null,
    @SerializedName("can_invite_users") val canInviteUsers: Boolean? = null,
    @SerializedName("can_pin_messages") val canPinMessages: Boolean? = null,
    @SerializedName("is_member") val isMember: Boolean? = null,
    @SerializedName("can_send_messages") val canSendMessages: Boolean? = null,
    @SerializedName("can_send_media_messages") val canSendMediaMessages: Boolean? = null,
    @SerializedName("can_send_polls") val canSendPolls: Boolean? = null,
    @SerializedName("can_send_other_messages") val canSendOtherMessages: Boolean? = null,
    @SerializedName("can_add_web_page_previews") val canAddWebPagePreviews: Boolean? = null
)
