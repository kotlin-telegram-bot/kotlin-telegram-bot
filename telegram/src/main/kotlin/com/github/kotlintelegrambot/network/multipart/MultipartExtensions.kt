package com.github.kotlintelegrambot.network.multipart

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.nio.file.Files

internal fun File.toMultipartBodyPart(
    partName: String = name,
    mediaType: String? = null
): MultipartBody.Part {
    val mimeType = (mediaType ?: Files.probeContentType(toPath()))?.let { MediaType.parse(it) }
    val requestBody = RequestBody.create(mimeType, this)

    return MultipartBody.Part.createFormData(partName, name, requestBody)
}

internal fun String.toMultipartBodyPart(partName: String): MultipartBody.Part = MultipartBody.Part.createFormData(partName, this)

internal fun <T> T.toMultipartBodyPart(partName: String): MultipartBody.Part = toString().toMultipartBodyPart(partName)

internal fun Iterable<String>.toMultipartBodyPart(partName: String): MultipartBody.Part =
    joinToString(separator = ",", prefix = "[", postfix = "]").toMultipartBodyPart(partName)

internal fun ByteArray.toMultipartBodyPart(
    partName: String,
    filename: String? = partName,
    mediaType: String? = null
): MultipartBody.Part {
    val mimeType = mediaType?.let { MediaType.parse(it) }
    val requestBody = RequestBody.create(mimeType, this)

    return MultipartBody.Part.createFormData(partName, filename ?: partName, requestBody)
}
