package com.github.kotlintelegrambot.network.retrofit.converters

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatId.ChannelUsername
import com.github.kotlintelegrambot.entities.ChatId.Id
import com.github.kotlintelegrambot.entities.Message
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.jupiter.api.Test
import retrofit2.Retrofit

class ChatIdConverterFactoryTest {

    private val retrofitMock = mockk<Retrofit>()
    private val sut = ChatIdConverterFactory()

    @Test
    fun `returns a null converter when the input type is not ChatId`() {
        val stringConverterForInt = sut.stringConverter(Int::class.java, emptyArray(), retrofitMock)
        val stringConverterForCharSequence = sut.stringConverter(CharSequence::class.java, emptyArray(), retrofitMock)
        val stringConverterForMessage = sut.stringConverter(Message::class.java, emptyArray(), retrofitMock)

        assertNull(stringConverterForInt)
        assertNull(stringConverterForCharSequence)
        assertNull(stringConverterForMessage)
    }

    @Test
    fun `returns a converter that converts ChatId Id to string`() {
        val converter = sut.stringConverter(ChatId::class.java, emptyArray(), retrofitMock)

        val chatId = 123L
        assertEquals(chatId.toString(), converter?.convert(Id(chatId)))
    }

    @Test
    fun `returns a converter that converts ChatId Username to string`() {
        val converter = sut.stringConverter(ChatId::class.java, emptyArray(), retrofitMock)

        val username = "@battlecruiser-operational"
        assertEquals(username, converter?.convert(ChannelUsername(username)))
    }
}
