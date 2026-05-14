package com.github.kotlintelegrambot.entities.business

import com.google.gson.annotations.SerializedName as Name

/**
 * Describes the opening hours of a business account.
 *
 * https://core.telegram.org/bots/api#businessopeninghours (Bot API 7.2)
 */
data class BusinessOpeningHours(
    @Name("time_zone_name") val timeZoneName: String,
    @Name("opening_hours") val openingHours: List<BusinessOpeningHoursInterval>,
)

/**
 * One time-interval inside a [BusinessOpeningHours] week. Minutes are counted from the start of
 * Monday in the chat's time zone (0 = Monday 00:00 UTC equivalent, 1440 = Tuesday 00:00, etc.).
 *
 * https://core.telegram.org/bots/api#businessopeninghoursinterval (Bot API 7.2)
 */
data class BusinessOpeningHoursInterval(
    @Name("opening_minute") val openingMinute: Int,
    @Name("closing_minute") val closingMinute: Int,
)
