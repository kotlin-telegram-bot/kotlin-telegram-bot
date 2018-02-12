package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class UserProfilePhotos(
    @Name("total_count") val totalCount: Int,
    val photos: List<List<PhotoSize>>
)