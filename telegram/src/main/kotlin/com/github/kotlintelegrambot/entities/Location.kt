package com.github.kotlintelegrambot.entities

data class Location(
    val longitude: Float,
    val latitude: Float,
    val heading: Int? = null
)
