package com.github.kotlintelegrambot.network.serialization.adapter

import com.github.kotlintelegrambot.entities.dice.Dice
import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import com.google.gson.GsonBuilder
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test

class DiceEmojiAdapterTest {

    @Test
    fun `DiceEmoji arguments are correctly deserialized`() {
        val sut = GsonBuilder().registerTypeAdapter(DiceEmoji::class.java, DiceEmojiAdapter()).create()

        val diceJson = """
                {
                    "emoji": "🎲",
                    "value": "5"
                }
        """.trimIndent()
        val deserializedDice = sut.fromJson(diceJson, Dice::class.java)

        val expectedEmoji = DiceEmoji.Dice
        assertEquals(expectedEmoji, deserializedDice.emoji)
    }
}
