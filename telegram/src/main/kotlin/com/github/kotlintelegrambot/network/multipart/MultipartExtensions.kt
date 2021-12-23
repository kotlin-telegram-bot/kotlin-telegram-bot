package com.github.kotlintelegrambot.network.multipart

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.nio.file.Files

internal fun File.toMultipartBodyPart(
    partName: String = name,
    mediaType: String? = null
): MultipartBody.Part {
    val mimeType = (mediaType ?: Files.probeContentType(toPath()))?.toMediaTypeOrNull()
    val requestBody = this.asRequestBody(mimeType)

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
    val mimeType = mediaType?.toMediaTypeOrNull()
    val requestBody = this.toRequestBody(mimeType)

    return MultipartBody.Part.createFormData(partName, filename ?: partName, requestBody)
}
