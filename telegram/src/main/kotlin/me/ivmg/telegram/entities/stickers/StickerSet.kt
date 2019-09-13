package me.ivmg.telegram.entities.stickers

import com.google.gson.annotations.SerializedName as Name

data class StickerSet(
    val name: String,
    val title: String,
    @Name("is_animated") val isAnimated: Boolean,
    @Name("contains_masks") val containsMasks: Boolean,
    @Name("stickers") val stickers: List<Sticker>
)
