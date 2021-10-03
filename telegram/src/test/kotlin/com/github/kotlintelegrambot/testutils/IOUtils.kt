package com.github.kotlintelegrambot.testutils

import java.io.File

inline fun <reified T> getFileAsStringFromResources(resName: String): String =
    getFileFromResources<T>(resName).readText()

inline fun <reified T> getFileFromResources(resName: String): File =
    File(T::class.java.getResource("/$resName")!!.file.decode())
