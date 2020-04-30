package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class Invoice(
    val title: String,
    val description: String,
    @Name("start_parameter") val startParameter: String,
    val currency: String,
    @Name("total_amount") val totalAmount: Int
)
