package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.files.Document
import com.google.gson.annotations.SerializedName

/**
 * Describes the type of a background. (Bot API 7.3)
 *
 * See https://core.telegram.org/bots/api#backgroundtype
 */
sealed class BackgroundType {
    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /** The background is automatically filled based on the selected colors. */
    data class Fill(
        @SerializedName("type") override val type: String = "fill",
        @SerializedName("fill") val fill: BackgroundFill,
        @SerializedName("dark_theme_dimming") val darkThemeDimming: Int,
    ) : BackgroundType()

    /** The background is a wallpaper in the JPEG format. */
    data class Wallpaper(
        @SerializedName("type") override val type: String = "wallpaper",
        @SerializedName("document") val document: Document,
        @SerializedName("dark_theme_dimming") val darkThemeDimming: Int,
        @SerializedName("is_blurred") val isBlurred: Boolean? = null,
        @SerializedName("is_moving") val isMoving: Boolean? = null,
    ) : BackgroundType()

    /** The background is a PNG or TGV (gzipped subset of SVG) pattern. */
    data class Pattern(
        @SerializedName("type") override val type: String = "pattern",
        @SerializedName("document") val document: Document,
        @SerializedName("fill") val fill: BackgroundFill,
        @SerializedName("intensity") val intensity: Int,
        @SerializedName("is_inverted") val isInverted: Boolean? = null,
        @SerializedName("is_moving") val isMoving: Boolean? = null,
    ) : BackgroundType()

    /** The background is taken directly from a built-in chat theme. */
    data class ChatTheme(
        @SerializedName("type") override val type: String = "chat_theme",
        @SerializedName("theme_name") val themeName: String,
    ) : BackgroundType()
}
