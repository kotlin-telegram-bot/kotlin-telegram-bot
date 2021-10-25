package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents an incoming inline query. When the user sends an empty query, your bot could return
 * some default or trending results.
 *
 * @property id unique identifier for this query.
 * @property from sender.
 * @property location sender location, only for bots that request user location.
 * @property query text of the query (up to 256 characters)
 * @property offset offset of the results to be returned, can be controlled by the bot.
 * @property chatType type of the chat, from which the inline query was sent. The chat type should
 * be always known for requests sent from official clients and most third-party clients, unless the
 * request was sent from a secret chat.
 */
data class InlineQuery(
    val id: String,
    val from: User,
    val location: Location? = null,
    val query: String,
    val offset: String,
    @SerializedName("chat_type") val chatType: ChatType? = null,
) {

    enum class ChatType {
        @SerializedName("sender") SENDER, // private chat with the inline query sender
        @SerializedName("private") PRIVATE,
        @SerializedName("group") GROUP,
        @SerializedName("supergroup") SUPERGROUP,
        @SerializedName("channel") CHANNEL,
    }
}
