package com.github.kotlintelegrambot.entities.polls

import com.github.kotlintelegrambot.entities.User
import com.google.gson.annotations.SerializedName

/**
 * Represents an answer of a user in a non-anonymous poll.
 * https://core.telegram.org/bots/api#poll_answer
 */
data class PollAnswer(
    @SerializedName(PollFields.POLL_ID) val pollId: String,
    @SerializedName(PollFields.USER) val user: User,
    @SerializedName(PollFields.OPTION_IDS) val optionIds: List<Int>
)
