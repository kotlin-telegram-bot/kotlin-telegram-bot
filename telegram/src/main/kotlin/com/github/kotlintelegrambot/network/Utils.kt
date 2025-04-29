package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.entities.TelegramFile
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.BufferedSink
import okio.source
import retrofit2.Call
import retrofit2.Response
import java.io.InputStream
import java.net.ProtocolException

fun <T> Call<T>.call(): Pair<Response<T?>?, Exception?> = try {
    Pair(execute(), null)
} catch (exception: Exception) {
    Pair(null, exception)
}

class ResponseError(val errorBody: ResponseBody?, val exception: Exception?)

fun <T> Pair<Response<T?>?, Exception?>.fold(response: (T?) -> Unit = {}, error: (ResponseError) -> Unit = {}) {
    if (first?.isSuccessful == true && first?.body() != null) {
        response(first!!.body()!!)
    } else {
        error(ResponseError(first?.errorBody(), second))
    }
}

fun <T, R> Pair<Response<T?>?, Exception?>.bimap(mapResponse: (T?) -> R, mapError: (ResponseError) -> R): R =
    if (first?.isSuccessful == true && first?.body() != null) {
        val response = first!!.body()!!
        mapResponse(response)
    } else {
        mapError(ResponseError(first?.errorBody(), second))
    }

fun InputStream.toRequestBody(
    contentType: MediaType?,
    contentLength: Long = -1,
): RequestBody = object : RequestBody() {
    override fun contentType(): MediaType? = contentType

    override fun contentLength(): Long = contentLength

    override fun writeTo(sink: BufferedSink) {
        this@toRequestBody.use {
            sink.writeAll(it.source())
        }
    }
}

fun TelegramFile.ByInputStream.asMultipartBodyPart(partName: String) = MultipartBody.Part.createFormData(partName, filename ?: partName, stream.toRequestBody(contentType, contentLength))
