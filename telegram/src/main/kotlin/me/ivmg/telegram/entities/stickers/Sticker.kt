package me.ivmg.telegram.entities.stickers

import com.google.gson.annotations.SerializedName as Name
import me.ivmg.telegram.entities.PhotoSize

data class Sticker(
    @Name("file_id") val fileId: String,
    val width: Int,
    val height: Int,
    val is_animated: Boolean,
    val thumb: PhotoSize?,
    val emoji: String?,
    @Name("set_name")val setName: String?,
    @Name("mask_position")val maskPosition: MaskPosition?,
    @Name("file_size") val fileSize: Int?
)
