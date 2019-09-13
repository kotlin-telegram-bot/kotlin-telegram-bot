package me.ivmg.telegram.entities.stickers

import com.google.gson.annotations.SerializedName

data class MaskPosition(
    val point: String,
    @SerializedName("x_shift") val xShift: Float,
    @SerializedName("y_shift") val yShift: Float,
    val scale: Float
)
