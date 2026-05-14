package com.github.kotlintelegrambot.entities.gifts

import com.google.gson.annotations.SerializedName as Name

/**
 * Describes the background of a gift. RGB integers (0xRRGGBB).
 *
 * https://core.telegram.org/bots/api#giftbackground
 */
data class GiftBackground(
    @Name("center_color") val centerColor: Int,
    @Name("edge_color") val edgeColor: Int,
    @Name("text_color") val textColor: Int,
)
