package com.github.kotlintelegrambot.entities.payments

public data class ShippingOption(
    val id: String,
    val title: String,
    val prices: List<LabeledPrice>
)
