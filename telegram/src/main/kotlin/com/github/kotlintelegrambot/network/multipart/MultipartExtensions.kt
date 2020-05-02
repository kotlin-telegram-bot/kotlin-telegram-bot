package com.github.kotlintelegrambot.network.multipart

import java.io.File
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

internal fun File.toMultipartBodyPart(partName: String = name, mediaType: String): MultipartBody.Part {
    val fileRequestBody = RequestBody.create(MediaType.parse(mediaType), this)
    return MultipartBody.Part.createFormData(partName, name, fileRequestBody)
}

internal fun String.toMultipartBodyPart(partName: String): MultipartBody.Part = MultipartBody.Part.createFormData(partName, this)

internal fun <T> T.toMultipartBodyPart(partName: String): MultipartBody.Part = toString().toMultipartBodyPart(partName)

internal fun Iterable<String>.toMultipartBodyPart(partName: String): MultipartBody.Part? =
    joinToString(separator = ",", prefix = "[", postfix = "]").toMultipartBodyPart(partName)
