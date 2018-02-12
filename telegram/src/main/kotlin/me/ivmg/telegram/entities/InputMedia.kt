package me.ivmg.telegram.entities

abstract class InputMedia {
    abstract val type: String
    abstract val media: String
    abstract val caption: String?
}