package com.github.kotlintelegrambot.network.retrofit.converters

import com.google.gson.annotations.SerializedName
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.Retrofit
import java.lang.IllegalStateException

class EnumRetrofitConverterFactoryTest {

    enum class RegularEnumAnnotated {
        @SerializedName("enumA")
        ENUM_A
    }

    enum class RegularEnum {
        ENUM_A
    }

    data class TestClass(
        val testAttr: Int
    ) {

        enum class InnerEnum {
            @SerializedName("enumA")
            ENUM_A
        }
    }

    private val retrofitMock = mockk<Retrofit>()
    private val sut = EnumRetrofitConverterFactory()

    @Test
    fun `non enum returns a null converter`() {
        val stringConverter = sut.stringConverter(Int::class.java, emptyArray(), retrofitMock)

        assertNull(stringConverter)
    }

    @Test
    fun `regular enum with values annotated with @SerializedName is correctly transformed to String`() {
        val stringConverter = sut.stringConverter(RegularEnumAnnotated::class.java, emptyArray(), retrofitMock)
        val stringFromRegularEnum = stringConverter?.convert(RegularEnumAnnotated.ENUM_A)

        val expectedString = "enumA"
        assertEquals(expectedString, stringFromRegularEnum)
    }

    @Test
    fun `regular enum with values not annotated with @SerializedName throws an error`() {
        val error = assertThrows<IllegalStateException> {
            val stringConverter = sut.stringConverter(RegularEnum::class.java, emptyArray(), retrofitMock)
            stringConverter?.convert(RegularEnum.ENUM_A)
        }

        val expectedErrorMessage = "cannot serialize ${RegularEnum::class.java} enum properly, please make sure it's annotated with @SerializedName"
        assertEquals(expectedErrorMessage, error.message)
    }

    @Test
    fun `inner enum with values annotated with @SerializedName is correctly transformed to String`() {
        val stringConverter = sut.stringConverter(TestClass.InnerEnum::class.java, emptyArray(), retrofitMock)
        val stringFromRegularEnum = stringConverter?.convert(TestClass.InnerEnum.ENUM_A)

        val expectedString = "enumA"
        assertEquals(expectedString, stringFromRegularEnum)
    }
}
