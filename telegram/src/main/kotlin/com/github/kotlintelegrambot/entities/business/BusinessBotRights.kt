package com.github.kotlintelegrambot.entities.business

import com.google.gson.annotations.SerializedName as Name

/**
 * Represents the rights of a business bot.
 *
 * https://core.telegram.org/bots/api#businessbotrights (Bot API 9.0)
 *
 * Supersedes the deprecated `BusinessConnection.can_reply` boolean.
 */
data class BusinessBotRights(
    @Name("can_reply") val canReply: Boolean? = null,
    @Name("can_read_messages") val canReadMessages: Boolean? = null,
    @Name("can_delete_outgoing_messages") val canDeleteOutgoingMessages: Boolean? = null,
    @Name("can_delete_all_messages") val canDeleteAllMessages: Boolean? = null,
    @Name("can_edit_name") val canEditName: Boolean? = null,
    @Name("can_edit_bio") val canEditBio: Boolean? = null,
    @Name("can_edit_profile_photo") val canEditProfilePhoto: Boolean? = null,
    @Name("can_edit_username") val canEditUsername: Boolean? = null,
    @Name("can_change_gift_settings") val canChangeGiftSettings: Boolean? = null,
    @Name("can_view_gifts_and_stars") val canViewGiftsAndStars: Boolean? = null,
    @Name("can_convert_gifts_to_stars") val canConvertGiftsToStars: Boolean? = null,
    @Name("can_transfer_and_upgrade_gifts") val canTransferAndUpgradeGifts: Boolean? = null,
    @Name("can_transfer_stars") val canTransferStars: Boolean? = null,
    @Name("can_manage_stories") val canManageStories: Boolean? = null,
)
