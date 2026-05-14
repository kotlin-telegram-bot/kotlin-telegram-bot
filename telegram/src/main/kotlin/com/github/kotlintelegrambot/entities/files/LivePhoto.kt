package com.github.kotlintelegrambot.entities.files

import com.google.gson.annotations.SerializedName

/**
 * Represents a live photo. (Bot API 10.0)
 *
 * See https://core.telegram.org/bots/api#livephoto
 */
data class LivePhoto(
    @SerializedName("file_id") val fileId: String,
    @SerializedName("file_unique_id") val fileUniqueId: String,
    @SerializedName("video") val video: Video,
    @SerializedName("photo") val photo: List<PhotoSize>,
)
