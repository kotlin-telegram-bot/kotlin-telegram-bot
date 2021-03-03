package com.github.kotlintelegrambot.network.retrofit.converters

import com.google.gson.annotations.SerializedName
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class EnumRetrofitConverterFactory : Converter.Factory() {

    override fun stringConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<Enum<*>, String>? =
        if (type is Class<*> && type.isEnum) {
            Converter { enum ->
                try {
                    enum.javaClass.getField(enum.name).getAnnotation(SerializedName::class.java)?.value
                } catch (e: NoSuchFieldException) {
                    enumSerializationError(type)
                } ?: enumSerializationError(type)
            }
        } else {
            null
        }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun enumSerializationError(type: Type?): Nothing = error("cannot serialize $type enum properly, please make sure it's annotated with @SerializedName")
}
