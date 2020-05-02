package com.github.kotlintelegrambot.testutils

import okhttp3.mockwebserver.RecordedRequest

// WARNING! Don't use this code to get multipart boundaries in real life
val RecordedRequest.multipartBoundary: String
    get() {
        val contentTypeHeader: String = headers["Content-Type"] ?: error("no Content-Type header")
        val boundary = contentTypeHeader.split(';')[1].split('=')[1]
        return boundary
    }
