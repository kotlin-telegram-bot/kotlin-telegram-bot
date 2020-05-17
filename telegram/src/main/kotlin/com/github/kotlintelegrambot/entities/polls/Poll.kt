package com.github.kotlintelegrambot.entities.polls

import com.github.kotlintelegrambot.entities.MessageEntity
import com.google.gson.annotations.SerializedName

/**
 * Contains information about a poll.
 * https://core.telegram.org/bots/api#poll
 */
data class Poll(
    @SerializedName(PollFields.ID) val id: Long,
    @SerializedName(PollFields.QUESTION) val question: String,
    @SerializedName(PollFields.OPTIONS) val options: List<PollOption>,
    @SerializedName(PollFields.TOTAL_VOTER_COUNT) val totalVoterCount: Int,
    @SerializedName(PollFields.IS_CLOSED) val isClosed: Boolean,
    @SerializedName(PollFields.IS_ANONYMOUS) val isAnonymous: Boolean,
    @SerializedName(PollFields.TYPE) val type: PollType,
    @SerializedName(PollFields.ALLOWS_MULTIPLE_ANSWERS) val allowsMultipleAnswers: Boolean,
    @SerializedName(PollFields.CORRECT_OPTION_ID) val correctOptionId: Int? = null,
    @SerializedName(PollFields.EXPLANATION) val explanation: String? = null,
    @SerializedName(PollFields.EXPLANATION_ENTITIES) val explanationEntities: List<MessageEntity>? = null,
    @SerializedName(PollFields.OPEN_PERIOD) val openPeriod: Int? = null,
    @SerializedName(PollFields.CLOSE_DATE) val closeData: Long? = null
)
