package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.reaction.ReactionType
import com.google.gson.annotations.SerializedName

/**
 * Represents a change of a reaction on a message performed by a user.
 *
 * See https://core.telegram.org/bots/api#messagereactionupdated
 */
data class MessageReactionUpdated(
    @SerializedName("chat") val chat: Chat,
    @SerializedName("message_id") val messageId: Long,
    @SerializedName("user") val user: User? = null,
    @SerializedName("actor_chat") val actorChat: Chat? = null,
    @SerializedName("date") val date: Long,
    @SerializedName("old_reaction") val oldReaction: List<ReactionType>,
    @SerializedName("new_reaction") val newReaction: List<ReactionType>,
)
