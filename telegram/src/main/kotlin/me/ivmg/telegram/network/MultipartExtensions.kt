package me.ivmg.telegram.network

import java.io.File
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

fun File.toMultipartBodyPart(
    partName: String,
    mediaType: String
): MultipartBody.Part {
    val fileRequestBody = RequestBody.create(MediaType.parse(mediaType), this)
    return MultipartBody.Part.createFormData(partName, name, fileRequestBody)
}

fun String.toMultipartBodyPart(partName: String): MultipartBody.Part =
    MultipartBody.Part.createFormData(partName, this)

fun Int?.toMultipartBodyPartOrNull(partName: String): MultipartBody.Part? =
    if (this != null) MultipartBody.Part.createFormData(partName, this.toString()) else null

fun Iterable<String>?.toMultipartBodyPartOrNull(partName: String): MultipartBody.Part? =
    this?.joinToString(separator = ",", prefix = "[", postfix = "]")?.toMultipartBodyPart(partName)
