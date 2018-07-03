package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class User(
    var id: Long,
    @Name("is_bot") val isBot: Boolean,
    @Name("first_name") val firstName: String,
    @Name("last_name") val lastName: String?,
    val username: String?,
    @Name("language_code") val languageCode: String?
)
