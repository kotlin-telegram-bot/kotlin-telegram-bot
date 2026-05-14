package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

/**
 * Defines the criteria used to request a suitable user.
 *
 * Bot API 6.5. Deprecated in 7.0 in favor of [KeyboardButtonRequestUsers].
 *
 * See https://core.telegram.org/bots/api#keyboardbuttonrequestuser
 */
@Deprecated(
    message = "Use KeyboardButtonRequestUsers instead (Bot API 7.0).",
    replaceWith = ReplaceWith("KeyboardButtonRequestUsers"),
)
data class KeyboardButtonRequestUser(
    @Name("request_id") val requestId: Int,
    @Name("user_is_bot") val userIsBot: Boolean? = null,
    @Name("user_is_premium") val userIsPremium: Boolean? = null,
)

/**
 * Defines the criteria used to request suitable users. The identifiers of the selected users will
 * be shared with the bot when the corresponding button is pressed.
 *
 * Bot API 7.0.
 *
 * See https://core.telegram.org/bots/api#keyboardbuttonrequestusers
 */
data class KeyboardButtonRequestUsers(
    @Name("request_id") val requestId: Int,
    @Name("user_is_bot") val userIsBot: Boolean? = null,
    @Name("user_is_premium") val userIsPremium: Boolean? = null,
    @Name("max_quantity") val maxQuantity: Int? = null,
    @Name("request_name") val requestName: Boolean? = null,
    @Name("request_username") val requestUsername: Boolean? = null,
    @Name("request_photo") val requestPhoto: Boolean? = null,
)

/**
 * Defines the criteria used to request a suitable chat. The identifier of the selected chat will
 * be shared with the bot when the corresponding button is pressed.
 *
 * Bot API 6.5, expanded in 7.2.
 *
 * See https://core.telegram.org/bots/api#keyboardbuttonrequestchat
 */
data class KeyboardButtonRequestChat(
    @Name("request_id") val requestId: Int,
    @Name("chat_is_channel") val chatIsChannel: Boolean,
    @Name("chat_is_forum") val chatIsForum: Boolean? = null,
    @Name("chat_has_username") val chatHasUsername: Boolean? = null,
    @Name("chat_is_created") val chatIsCreated: Boolean? = null,
    @Name("user_administrator_rights") val userAdministratorRights: ChatAdministratorRights? = null,
    @Name("bot_administrator_rights") val botAdministratorRights: ChatAdministratorRights? = null,
    @Name("bot_is_member") val botIsMember: Boolean? = null,
    @Name("request_title") val requestTitle: Boolean? = null,
    @Name("request_username") val requestUsername: Boolean? = null,
    @Name("request_photo") val requestPhoto: Boolean? = null,
)
