package com.github.kotlintelegrambot.entities.polls

import com.google.gson.annotations.SerializedName

enum class PollType {
    @SerializedName("quiz") QUIZ,
    @SerializedName("regular") REGULAR;
}
