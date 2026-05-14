package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

/**
 * This object represents a forum topic. (Bot API 6.3)
 *
 * See https://core.telegram.org/bots/api#forumtopic
 */
data class ForumTopic(
    @Name("message_thread_id") val messageThreadId: Long,
    val name: String,
    @Name("icon_color") val iconColor: Int,
    @Name("icon_custom_emoji_id") val iconCustomEmojiId: String? = null,
)

/**
 * This object represents a service message about a new forum topic created in the chat. (Bot API 6.3)
 *
 * See https://core.telegram.org/bots/api#forumtopiccreated
 */
data class ForumTopicCreated(
    val name: String,
    @Name("icon_color") val iconColor: Int,
    @Name("icon_custom_emoji_id") val iconCustomEmojiId: String? = null,
)

/** Service message about a forum topic closed in the chat. https://core.telegram.org/bots/api#forumtopicclosed */
data object ForumTopicClosed

/** Service message about a forum topic reopened in the chat. https://core.telegram.org/bots/api#forumtopicreopened */
data object ForumTopicReopened

/**
 * This object represents a service message about an edited forum topic. (Bot API 6.4)
 *
 * See https://core.telegram.org/bots/api#forumtopicedited
 */
data class ForumTopicEdited(
    val name: String? = null,
    @Name("icon_custom_emoji_id") val iconCustomEmojiId: String? = null,
)

/** Service message: General forum topic hidden. https://core.telegram.org/bots/api#generalforumtopichidden */
data object GeneralForumTopicHidden

/** Service message: General forum topic unhidden. https://core.telegram.org/bots/api#generalforumtopicunhidden */
data object GeneralForumTopicUnhidden

/**
 * This object represents a service message about a user allowing a bot to write messages after
 * adding the bot to the attachment menu, launching a Web App from a link, or accepting an explicit
 * request from a Web App sent by the method requestWriteAccess. (Bot API 6.4, expanded in 7.0)
 *
 * See https://core.telegram.org/bots/api#writeaccessallowed
 */
data class WriteAccessAllowed(
    @Name("from_request") val fromRequest: Boolean? = null,
    @Name("web_app_name") val webAppName: String? = null,
    @Name("from_attachment_menu") val fromAttachmentMenu: Boolean? = null,
)
