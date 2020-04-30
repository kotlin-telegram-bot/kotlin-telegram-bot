package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class ForceReply(
    @Name("force_reply") val forceReply: Boolean,
    val selective: Boolean
)
