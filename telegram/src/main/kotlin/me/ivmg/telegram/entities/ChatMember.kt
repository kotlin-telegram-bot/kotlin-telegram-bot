package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class ChatMember(
    val user: User,
    val status: String,
    @Name("until_date") val forceReply: Int? = null,
    @Name("can_be_edited") val canBeEdited: Boolean? = null,
    @Name("can_change_info") val canChangeInfo: Boolean? = null,
    @Name("can_post_messages") val canPostMessages: Boolean? = null,
    @Name("can_edit_messages") val canEditMessages: Boolean? = null,
    @Name("can_delete_messages") val canDeleteMessages: Boolean? = null,
    @Name("can_invite_users") val canInviteUsers: Boolean? = null,
    @Name("can_restrict_members") val canRestrictMembers: Boolean? = null,
    @Name("can_pin_messages") val canPinMessages: Boolean? = null,
    @Name("is_member") val isMember: Boolean? = null,
    @Name("can_promote_members") val canPromoteMembers: Boolean? = null,
    @Name("can_send_messages") val canSendMessages: Boolean? = null,
    @Name("can_send_media_messages") val canSendMediaMessages: Boolean? = null,
    @Name("can_send_other_messages") val canSendOtherMessages: Boolean? = null,
    @Name("can_add_web_page_previews") val canAddWebPagePreviews: Boolean? = null
)
