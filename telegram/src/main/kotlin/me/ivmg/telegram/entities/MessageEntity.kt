package me.ivmg.telegram.entities

data class MessageEntity(
    val type: String,
    val offset: Int,
    val length: Int,
    val url: String?,
    val user: User?
)