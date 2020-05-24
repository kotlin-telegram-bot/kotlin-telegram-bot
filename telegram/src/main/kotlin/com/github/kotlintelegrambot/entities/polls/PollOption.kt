package com.github.kotlintelegrambot.entities.polls

import com.google.gson.annotations.SerializedName

/**
 * Contains information about one answer option in a poll.
 * https://core.telegram.org/bots/api#polloption
 */
data class PollOption(
    @SerializedName(PollFields.TEXT) val text: String,
    @SerializedName(PollFields.VOTER_COUNT) val voterCount: Int
)
