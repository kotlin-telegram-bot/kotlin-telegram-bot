package com.github.kotlintelegrambot.types

abstract class ConsumableObject {

    var consumed: Boolean = false
        private set

    fun consume() {
        if (consumed) {
            throw IllegalStateException("This object has already been consumed.")
        }
        consumed = true
    }
}
