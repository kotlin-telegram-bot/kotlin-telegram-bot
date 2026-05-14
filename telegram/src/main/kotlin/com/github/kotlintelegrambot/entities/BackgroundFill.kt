package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Describes the way a background is filled based on the selected colors. (Bot API 7.3)
 *
 * See https://core.telegram.org/bots/api#backgroundfill
 */
sealed class BackgroundFill {

    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /** The background is filled using the selected color. */
    data class Solid(
        @SerializedName("type") override val type: String = "solid",
        @SerializedName("color") val color: Long,
    ) : BackgroundFill()

    /** The background is a gradient fill. */
    data class Gradient(
        @SerializedName("type") override val type: String = "gradient",
        @SerializedName("top_color") val topColor: Long,
        @SerializedName("bottom_color") val bottomColor: Long,
        @SerializedName("rotation_angle") val rotationAngle: Int,
    ) : BackgroundFill()

    /** The background is a freeform gradient that rotates after every message in the chat. */
    data class FreeformGradient(
        @SerializedName("type") override val type: String = "freeform_gradient",
        @SerializedName("colors") val colors: List<Long>,
    ) : BackgroundFill()
}
