package com.github.kotlintelegrambot.entities

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName as Name

data class ForceReplyMarkup(
    @Name("force_reply") val forceReply: Boolean = true,
    val selective: Boolean? = null
) : ReplyMarkup {
    private companion object {
        val GSON = Gson()
    }

    override fun toString(): String = GSON.toJson(this)
}
