package me.ivmg.telegram.entities

data class InlineQuery(
    val id: String,
    val from: User,
    val location: Location?,
    val query: String,
    val offset: String
)