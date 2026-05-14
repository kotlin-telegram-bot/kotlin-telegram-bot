package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.entities.stickers.Sticker
import com.google.gson.annotations.SerializedName

/**
 * Describes a unique gift that was upgraded from a regular gift. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#uniquegift
 */
data class UniqueGift(
    @SerializedName("base_name") val baseName: String,
    @SerializedName("name") val name: String,
    @SerializedName("number") val number: Int,
    @SerializedName("model") val model: UniqueGiftModel,
    @SerializedName("symbol") val symbol: UniqueGiftSymbol,
    @SerializedName("backdrop") val backdrop: UniqueGiftBackdrop,
)

/**
 * Describes the model of a unique gift.
 *
 * See https://core.telegram.org/bots/api#uniquegiftmodel
 */
data class UniqueGiftModel(
    @SerializedName("name") val name: String,
    @SerializedName("sticker") val sticker: Sticker,
    @SerializedName("rarity_per_mille") val rarityPerMille: Int,
)

/**
 * Describes the symbol shown on the pattern of a unique gift.
 *
 * See https://core.telegram.org/bots/api#uniquegiftsymbol
 */
data class UniqueGiftSymbol(
    @SerializedName("name") val name: String,
    @SerializedName("sticker") val sticker: Sticker,
    @SerializedName("rarity_per_mille") val rarityPerMille: Int,
)

/**
 * Describes the backdrop of a unique gift.
 *
 * See https://core.telegram.org/bots/api#uniquegiftbackdrop
 */
data class UniqueGiftBackdrop(
    @SerializedName("name") val name: String,
    @SerializedName("colors") val colors: UniqueGiftBackdropColors,
    @SerializedName("rarity_per_mille") val rarityPerMille: Int,
)

/**
 * Describes the colors of the backdrop of a unique gift.
 *
 * See https://core.telegram.org/bots/api#uniquegiftbackdropcolors
 */
data class UniqueGiftBackdropColors(
    @SerializedName("center_color") val centerColor: Long,
    @SerializedName("edge_color") val edgeColor: Long,
    @SerializedName("symbol_color") val symbolColor: Long,
    @SerializedName("text_color") val textColor: Long,
)
