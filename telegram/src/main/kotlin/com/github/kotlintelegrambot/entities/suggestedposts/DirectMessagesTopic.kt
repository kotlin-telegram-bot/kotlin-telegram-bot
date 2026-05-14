package com.github.kotlintelegrambot.entities.suggestedposts

import com.google.gson.annotations.SerializedName

/**
 * Describes a direct messages topic. (Bot API 9.2)
 *
 * See https://core.telegram.org/bots/api#directmessagestopic
 */
data class DirectMessagesTopic(
    @SerializedName("topic_id") val topicId: Int,
    @SerializedName("user") val user: com.github.kotlintelegrambot.entities.User? = null,
)
