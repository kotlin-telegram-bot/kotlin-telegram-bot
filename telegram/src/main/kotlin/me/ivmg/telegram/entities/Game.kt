package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class Game(
    val title: String,
    val description: String,
    val photo: List<PhotoSize>,
    val text: String?,
    @Name("text_entities") val textEntities: List<MessageEntity>?,
    val animation: Animation?
)