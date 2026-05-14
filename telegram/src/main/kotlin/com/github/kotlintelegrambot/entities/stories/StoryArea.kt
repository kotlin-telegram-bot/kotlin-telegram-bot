package com.github.kotlintelegrambot.entities.stories

import com.github.kotlintelegrambot.entities.LocationAddress
import com.github.kotlintelegrambot.entities.reaction.ReactionType
import com.google.gson.annotations.SerializedName

/**
 * Describes a clickable area on a story media. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#storyarea
 */
data class StoryArea(
    @SerializedName("position") val position: StoryAreaPosition,
    @SerializedName("type") val type: StoryAreaType,
)

/**
 * Describes the position of a clickable area within a story.
 *
 * See https://core.telegram.org/bots/api#storyareaposition
 */
data class StoryAreaPosition(
    @SerializedName("x_percentage") val xPercentage: Float,
    @SerializedName("y_percentage") val yPercentage: Float,
    @SerializedName("width_percentage") val widthPercentage: Float,
    @SerializedName("height_percentage") val heightPercentage: Float,
    @SerializedName("rotation_angle") val rotationAngle: Float,
    @SerializedName("corner_radius_percentage") val cornerRadiusPercentage: Float,
)

/**
 * Describes the type of a clickable area on a story.
 *
 * See https://core.telegram.org/bots/api#storyareatype
 */
sealed class StoryAreaType {
    abstract val type: String

    /** Story area pointing to a location. */
    data class Location(
        @SerializedName("type") override val type: String = "location",
        @SerializedName("latitude") val latitude: Float,
        @SerializedName("longitude") val longitude: Float,
        @SerializedName("address") val address: LocationAddress? = null,
    ) : StoryAreaType()

    /** Story area pointing to a suggested reaction. */
    data class SuggestedReaction(
        @SerializedName("type") override val type: String = "suggested_reaction",
        @SerializedName("reaction_type") val reactionType: ReactionType,
        @SerializedName("is_dark") val isDark: Boolean? = null,
        @SerializedName("is_flipped") val isFlipped: Boolean? = null,
    ) : StoryAreaType()

    /** Story area pointing to an HTTP or `tg://` URL. */
    data class Link(
        @SerializedName("type") override val type: String = "link",
        @SerializedName("url") val url: String,
    ) : StoryAreaType()

    /** Story area showing a weather forecast. */
    data class Weather(
        @SerializedName("type") override val type: String = "weather",
        @SerializedName("temperature") val temperature: Float,
        @SerializedName("emoji") val emoji: String,
        @SerializedName("background_color") val backgroundColor: Int,
    ) : StoryAreaType()

    /** Story area pointing to a unique gift. */
    data class UniqueGift(
        @SerializedName("type") override val type: String = "unique_gift",
        @SerializedName("name") val name: String,
    ) : StoryAreaType()
}
