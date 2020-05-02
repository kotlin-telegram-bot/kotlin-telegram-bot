package com.github.kotlintelegrambot.entities.inputmedia

import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.anyByFileIdTelegramFile

fun anyInputMediaVideo(
    media: TelegramFile = anyByFileIdTelegramFile(),
    thumb: TelegramFile.ByFile? = null
): InputMediaVideo = InputMediaVideo(
    media = media,
    caption = null,
    parseMode = null,
    thumb = thumb,
    width = null,
    height = null,
    duration = null,
    supportsStreaming = null
)

fun anyInputMediaPhoto(
    media: TelegramFile = anyByFileIdTelegramFile()
): InputMediaPhoto = InputMediaPhoto(
    media = media,
    caption = null,
    parseMode = null
)
