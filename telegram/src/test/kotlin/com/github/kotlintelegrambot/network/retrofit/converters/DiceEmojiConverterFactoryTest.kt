package com.github.kotlintelegrambot.network.retrofit.converters

import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import com.github.kotlintelegrambot.entities.dice.DiceEmoji.Dartboard
import com.github.kotlintelegrambot.entities.dice.DiceEmoji.Dice
import com.github.kotlintelegrambot.entities.dice.DiceEmoji.Other
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import retrofit2.Retrofit

class DiceEmojiConverterFactoryTest {

    private val retrofitMock = mockk<Retrofit>()
    private val sut = DiceEmojiConverterFactory()

    @Test
    fun `returns a null converter when the input type is not DiceEmoji`() {
        val stringConverterForInt = sut.stringConverter(Int::class.java, emptyArray(), retrofitMock)
        val stringConverterForCharSequence = sut.stringConverter(CharSequence::class.java, emptyArray(), retrofitMock)
        val stringConverterForMessage = sut.stringConverter(Message::class.java, emptyArray(), retrofitMock)

        assertNull(stringConverterForInt)
        assertNull(stringConverterForCharSequence)
        assertNull(stringConverterForMessage)
    }

    @Test
    fun `returns a converter that transforms DiceEmoji to correspondent emoji string when the input type is DiceEmoji`() {
        val genericStringConverter = sut.stringConverter(DiceEmoji::class.java, emptyArray(), retrofitMock)

        assertEquals(Dice.emojiValue, genericStringConverter?.convert(Dice))
        assertEquals(Dartboard.emojiValue, genericStringConverter?.convert(Dartboard))
        val anyOtherEmoji = "\uD83D\uDE31" // face screaming emoji -> 😱
        assertEquals(
            anyOtherEmoji,
            genericStringConverter?.convert(Other(anyOtherEmoji))
        )
    }

    @Test
    fun `returns a converter that transforms Dice to correspondent emoji string when the input type is Dice`() {
        val stringConverterForDice = sut.stringConverter(Dice::class.java, emptyArray(), retrofitMock)

        assertEquals(Dice.emojiValue, stringConverterForDice?.convert(Dice))
    }

    @Test
    fun `returns a converter that transforms Dartboard to correspondent emoji string when the input type is Dartboard`() {
        val stringConverterForDartboard = sut.stringConverter(Dartboard::class.java, emptyArray(), retrofitMock)

        assertEquals(
            Dartboard.emojiValue,
            stringConverterForDartboard?.convert(Dartboard)
        )
    }

    @Test
    fun `returns a converter that transforms Other to correspondent emoji string when the input type is Other`() {
        val stringConverterForOther = sut.stringConverter(Other::class.java, emptyArray(), retrofitMock)

        val anyOtherEmoji = "\uD83D\uDE31" // face screaming emoji -> 😱
        assertEquals(
            anyOtherEmoji,
            stringConverterForOther?.convert(Other(anyOtherEmoji))
        )
    }
}
