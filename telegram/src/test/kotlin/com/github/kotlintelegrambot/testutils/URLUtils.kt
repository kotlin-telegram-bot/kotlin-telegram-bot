package com.github.kotlintelegrambot.testutils

import java.net.URLDecoder

fun String.decode(characterEncoding: String = "UTF-8"): String = URLDecoder.decode(this, characterEncoding)
