package com.github.kotlintelegrambot.types

public abstract class ConsumableObject {

    public var consumed: Boolean = false
        private set

    public fun consume() {
        if (consumed) {
            throw IllegalStateException("This object has already been consumed.")
        }
        consumed = true
    }
}
