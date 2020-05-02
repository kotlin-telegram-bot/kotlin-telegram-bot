package com.github.kotlintelegrambot.entities

import java.io.File

private const val ANY_FILE_ID = "124n32jn"
fun anyByFileIdTelegramFile(fileId: String = ANY_FILE_ID): TelegramFile = TelegramFile.ByFileId(fileId)

private const val ANY_FILE_URL = "https://www.rukon.com/rukaina"
fun anyByUrlTelegramFile(fileUrl: String = ANY_FILE_URL): TelegramFile = TelegramFile.ByUrl(fileUrl)

fun anyByFileTelegramFile(file: File): TelegramFile = TelegramFile.ByFile(file)
