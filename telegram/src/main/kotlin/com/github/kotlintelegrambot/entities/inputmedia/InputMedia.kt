package com.github.kotlintelegrambot.entities.inputmedia

abstract class InputMedia {
    abstract val type: String
    abstract val media: String
    abstract val caption: String?
}
