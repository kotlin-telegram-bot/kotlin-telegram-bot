package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class ForumTopic(
    @Name("message_thread_id") val messageThreadId: Long,
    val name: String,
    @Name("icon_color") val iconColor: Int,
    @Name("icon_custom_emoji_id") val iconCustomEmojiId: String,
)
