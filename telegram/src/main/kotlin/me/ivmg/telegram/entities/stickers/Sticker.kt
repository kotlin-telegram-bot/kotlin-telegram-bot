package me.ivmg.telegram.entities.stickers

import com.google.gson.annotations.SerializedName as Name
import me.ivmg.telegram.entities.PhotoSize

data class Sticker(
    @Name("file_id") val fileId: String,
    val width: Int,
    val height: Int,
    @Name("is_animated") val isAnimated: Boolean,
    val thumb: PhotoSize? = null,
    val emoji: String?,
    @Name("set_name")val setName: String? = null,
    @Name("mask_position")val maskPosition: MaskPosition? = null,
    @Name("file_size") val fileSize: Int? = null
)
