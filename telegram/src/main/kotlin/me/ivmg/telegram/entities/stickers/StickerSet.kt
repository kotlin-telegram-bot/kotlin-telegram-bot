package me.ivmg.telegram.entities.stickers

import com.google.gson.annotations.SerializedName

data class StickerSet(
    val name: String,
    val title: String,
    @SerializedName("file_size") val isAnimated: Boolean,
    @SerializedName("contains_masks") val containsMasks: Boolean,
    @SerializedName("stickers") val stickers: List<Sticker>
)
