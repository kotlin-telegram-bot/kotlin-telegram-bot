package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

data class WebAppData(
    @SerializedName("data") val data: String? = null,
    @SerializedName("button_text") val buttonText: String? = null,
)
