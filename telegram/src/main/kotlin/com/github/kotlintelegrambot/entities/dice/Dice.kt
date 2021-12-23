package com.github.kotlintelegrambot.entities.dice

import com.google.gson.annotations.SerializedName

/**
 * Represents an animated emoji that displays a random value.
 * https://core.telegram.org/bots/api#dice
 */
public data class Dice(
    @SerializedName(DiceFields.EMOJI) val emoji: DiceEmoji,
    @SerializedName(DiceFields.VALUE) val value: Int
)

public sealed class DiceEmoji {
    public abstract val emojiValue: String

    public object Dice : DiceEmoji() {
        override val emojiValue: String
            get() = "🎲"
    }

    public object Dartboard : DiceEmoji() {
        override val emojiValue: String
            get() = "🎯"
    }

    public object Basketball : DiceEmoji() {
        override val emojiValue: String
            get() = "🏀"
    }

    public object Football : DiceEmoji() {
        override val emojiValue: String
            get() = "⚽️"
    }

    public object SlotMachine : DiceEmoji() {
        override val emojiValue: String
            get() = "🎰"
    }

    public object Bowling : DiceEmoji() {
        override val emojiValue: String
            get() = "🎳"
    }

    // Currently, not supported, adding it just in case Telegram Bot API
    // starts supporting new emojis for the dice in the future
    public data class Other(override val emojiValue: String) : DiceEmoji()

    public companion object {
        public fun fromString(emoji: String): DiceEmoji = when (emoji) {
            Dice.emojiValue -> Dice
            Dartboard.emojiValue -> Dartboard
            Basketball.emojiValue -> Basketball
            Football.emojiValue -> Football
            SlotMachine.emojiValue -> SlotMachine
            Bowling.emojiValue -> Bowling
            else -> Other(emoji)
        }
    }
}
