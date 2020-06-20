package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.files.Animation
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.google.gson.annotations.SerializedName as Name

data class Game(
    val title: String,
    val description: String,
    val photo: List<PhotoSize>,
    val text: String? = null,
    @Name("text_entities") val textEntities: List<MessageEntity>? = null,
    val animation: Animation? = null
)
