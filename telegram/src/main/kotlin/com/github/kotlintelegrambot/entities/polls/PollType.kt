package com.github.kotlintelegrambot.entities.polls

import com.google.gson.annotations.SerializedName

public enum class PollType {
    @SerializedName("quiz") QUIZ,
    @SerializedName("regular") REGULAR;
}
