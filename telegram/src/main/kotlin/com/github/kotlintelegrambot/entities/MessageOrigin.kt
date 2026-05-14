package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Describes the origin of a forwarded message. (Bot API 7.0)
 *
 * Replaces the legacy `forward_from`, `forward_from_chat`, `forward_from_message_id`,
 * `forward_signature`, `forward_sender_name` and `forward_date` fields on [Message].
 *
 * See https://core.telegram.org/bots/api#messageorigin
 */
sealed class MessageOrigin {
    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /** Date the message was sent originally (Unix time). */
    abstract val date: Long

    /** The message was originally sent by a known user. */
    data class User(
        @SerializedName("type") override val type: String = "user",
        @SerializedName("date") override val date: Long,
        @SerializedName("sender_user") val senderUser: com.github.kotlintelegrambot.entities.User,
    ) : MessageOrigin()

    /**
     * The message was originally sent by a user who has hidden their profile, or by a user without a
     * username.
     */
    data class HiddenUser(
        @SerializedName("type") override val type: String = "hidden_user",
        @SerializedName("date") override val date: Long,
        @SerializedName("sender_user_name") val senderUserName: String,
    ) : MessageOrigin()

    /** The message was originally sent on behalf of a chat to a group chat. */
    data class Chat(
        @SerializedName("type") override val type: String = "chat",
        @SerializedName("date") override val date: Long,
        @SerializedName("sender_chat") val senderChat: com.github.kotlintelegrambot.entities.Chat,
        @SerializedName("author_signature") val authorSignature: String? = null,
    ) : MessageOrigin()

    /** The message was originally sent to a channel chat. */
    data class Channel(
        @SerializedName("type") override val type: String = "channel",
        @SerializedName("date") override val date: Long,
        @SerializedName("chat") val chat: com.github.kotlintelegrambot.entities.Chat,
        @SerializedName("message_id") val messageId: Long,
        @SerializedName("author_signature") val authorSignature: String? = null,
    ) : MessageOrigin()
}
