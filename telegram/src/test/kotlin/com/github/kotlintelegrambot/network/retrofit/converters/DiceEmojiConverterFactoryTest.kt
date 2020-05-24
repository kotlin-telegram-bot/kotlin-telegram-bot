package com.github.kotlintelegrambot.network.retrofit.converters

import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
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

        assertEquals(DiceEmoji.Dice.emojiValue, genericStringConverter?.convert(DiceEmoji.Dice))
        assertEquals(DiceEmoji.Dartboard.emojiValue, genericStringConverter?.convert(DiceEmoji.Dartboard))
        val anyOtherEmoji = "\uD83D\uDE31" // face screaming emoji -> 😱
        assertEquals(anyOtherEmoji, genericStringConverter?.convert(DiceEmoji.Other(anyOtherEmoji)))
    }

    @Test
    fun `returns a converter that transforms Dice to correspondent emoji string when the input type is Dice`() {
        val stringConverterForDice = sut.stringConverter(DiceEmoji.Dice::class.java, emptyArray(), retrofitMock)

        assertEquals(DiceEmoji.Dice.emojiValue, stringConverterForDice?.convert(DiceEmoji.Dice))
    }

    @Test
    fun `returns a converter that transforms Dartboard to correspondent emoji string when the input type is Dartboard`() {
        val stringConverterForDartboard = sut.stringConverter(DiceEmoji.Dartboard::class.java, emptyArray(), retrofitMock)

        assertEquals(DiceEmoji.Dartboard.emojiValue, stringConverterForDartboard?.convert(DiceEmoji.Dartboard))
    }

    @Test
    fun `returns a converter that transforms Other to correspondent emoji string when the input type is Other`() {
        val stringConverterForOther = sut.stringConverter(DiceEmoji.Other::class.java, emptyArray(), retrofitMock)

        val anyOtherEmoji = "\uD83D\uDE31" // face screaming emoji -> 😱
        assertEquals(anyOtherEmoji, stringConverterForOther?.convert(DiceEmoji.Other(anyOtherEmoji)))
    }
}
