package com.github.kotlintelegrambot.testutils

import okhttp3.mockwebserver.RecordedRequest

// WARNING! Don't use this code to get multipart boundaries in real life
val RecordedRequest.multipartBoundary: String
    get() {
        val contentTypeHeader: String = headers["Content-Type"] ?: error("no Content-Type header")
        val boundary = contentTypeHeader.split(';')[1].split('=')[1]
        return boundary
    }

val RecordedRequest.apiMethodName: String?
    get() = path?.split("/")?.lastOrNull()?.split("?")?.firstOrNull()

val RecordedRequest.queryParams: String?
    get() {
        val parts = path?.split("?")
        return when {
            parts == null -> null
            parts.size == 1 -> null
            else -> parts.lastOrNull()
        }
    }

val RecordedRequest.decodedBody: String
    get() = body.readUtf8().decode()
