package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

/**
 * Describes the rating of a user based on their Telegram Star spendings. (Bot API 9.3)
 *
 * https://core.telegram.org/bots/api#userrating
 *
 * @property level Current level of the user. Higher = more trustworthy; a negative level is likely
 *   reason for concern.
 * @property rating Numerical value of the user's rating; the higher the rating, the better.
 * @property currentLevelRating The rating value required to get the current level.
 * @property nextLevelRating The rating value required to get to the next level; omitted if the
 *   maximum level was reached.
 */
data class UserRating(
    @Name("level") val level: Int,
    @Name("rating") val rating: Int,
    @Name("current_level_rating") val currentLevelRating: Int,
    @Name("next_level_rating") val nextLevelRating: Int? = null,
)
