package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class ChatPermissions(
    @Name("can_send_messages") val canSendMessages: Boolean? = null,
    @Name("can_send_media_messages") val canSendMediaMessages: Boolean? = null,
    @Name("can_send_polls") val canSendPolls: Boolean? = null,
    @Name("can_send_other_messages") val canSendOtherMessages: Boolean? = null,
    @Name("can_add_web_page_previews") val canAddWebPagePreviews: Boolean? = null,
    @Name("can_change_info") val canChangeInfo: Boolean? = null,
    @Name("can_invite_users") val canInviteUsers: Boolean? = null,
    @Name("can_pin_messages") val canPinMessages: Boolean? = null
)
