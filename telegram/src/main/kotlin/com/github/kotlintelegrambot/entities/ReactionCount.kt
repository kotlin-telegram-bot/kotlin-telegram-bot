package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.reaction.ReactionType
import com.google.gson.annotations.SerializedName

/**
 * Represents a reaction added to a message along with the number of times it was added.
 *
 * See https://core.telegram.org/bots/api#reactioncount
 */
data class ReactionCount(
    @SerializedName("type") val type: ReactionType,
    @SerializedName("total_count") val totalCount: Int,
)
