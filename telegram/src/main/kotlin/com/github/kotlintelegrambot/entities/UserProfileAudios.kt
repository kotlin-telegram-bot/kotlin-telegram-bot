package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a list of audio files of a user. (Bot API 9.4)
 *
 * See https://core.telegram.org/bots/api#userprofileaudios
 */
data class UserProfileAudios(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("audios") val audios: List<com.github.kotlintelegrambot.entities.files.Audio>,
)
