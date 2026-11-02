package com.github.kotlintelegrambot.entities.botcommandscope

import com.google.gson.annotations.SerializedName

/**
 * Represents the scope to which bot commands are applied. (Bot API 6.0)
 *
 * See https://core.telegram.org/bots/api#botcommandscope
 */
sealed class BotCommandScope {
    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /**
     * The default scope of bot commands. Used when no commands are set for the
     * user's chat, language or scope.
     */
    data class Default(
        @SerializedName("type") override val type: String = "default",
    ) : BotCommandScope()

    /** Covers all private chats. */
    data class AllPrivateChats(
        @SerializedName("type") override val type: String = "all_private_chats",
    ) : BotCommandScope()

    /** Covers all group and supergroup chats. */
    data class AllGroupChats(
        @SerializedName("type") override val type: String = "all_group_chats",
    ) : BotCommandScope()

    /** Covers all group and supergroup chat administrators. */
    data class AllChatAdministrators(
        @SerializedName("type") override val type: String = "all_chat_administrators",
    ) : BotCommandScope()

    /**
     * Covers a specific chat.
     *
     * @param chatId Unique identifier of the target chat.
     */
    data class Chat(
        @SerializedName("type") override val type: String = "chat",
        @SerializedName("chat_id") val chatId: Long,
    ) : BotCommandScope()

    /**
     * Covers all administrators of a specific group or supergroup chat.
     *
     * @param chatId Unique identifier of the target chat.
     */
    data class ChatAdministrators(
        @SerializedName("type") override val type: String = "chat_administrators",
        @SerializedName("chat_id") val chatId: Long,
    ) : BotCommandScope()

    /**
     * Covers a specific member of a group or supergroup chat.
     *
     * @param chatId Unique identifier of the target chat.
     * @param userId Unique identifier of the target user.
     */
    data class ChatMember(
        @SerializedName("type") override val type: String = "chat_member",
        @SerializedName("chat_id") val chatId: Long,
        @SerializedName("user_id") val userId: Long,
    ) : BotCommandScope()
}
