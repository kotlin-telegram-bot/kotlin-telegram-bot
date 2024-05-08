package com.github.kotlintelegrambot.entities.reaction

import com.google.gson.annotations.SerializedName

sealed class ReactionType(
    val type: String,
) {

    data class Emoji(
        val emoji: String,
    ) : ReactionType(
        "emoji",
    )

    data class Custom(
        @SerializedName("custom_emoji_id") val customEmojiId: String,
    ) : ReactionType(
        "custom_emoji",
    )
}
