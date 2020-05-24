package com.github.kotlintelegrambot.entities.dice

import com.google.gson.annotations.SerializedName

/**
 * Represents a dice with a random value from 1 to 6 for currently supported base emoji.
 * https://core.telegram.org/bots/api#dice
 */
data class Dice(
    @SerializedName(DiceFields.EMOJI) val emoji: DiceEmoji,
    @SerializedName(DiceFields.VALUE) val value: Int
)

sealed class DiceEmoji {
    abstract val emojiValue: String

    object Dice : DiceEmoji() {
        override val emojiValue: String
            get() = "🎲"
    }

    object Dartboard : DiceEmoji() {
        override val emojiValue: String
            get() = "🎯"
    }

    // Currently not supported, adding it just in case Telegram Bot API
    // starts supporting new emojis for the dice in the future
    data class Other(override val emojiValue: String) : DiceEmoji()

    companion object {
        fun fromString(emoji: String): DiceEmoji = when (emoji) {
            Dice.emojiValue -> Dice
            Dartboard.emojiValue -> Dartboard
            else -> Other(emoji)
        }
    }
}
