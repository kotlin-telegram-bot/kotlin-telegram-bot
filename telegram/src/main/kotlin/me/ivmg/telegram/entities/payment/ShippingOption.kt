package me.ivmg.telegram.entities.payment

data class ShippingOption(
    val id: String,
    val title: String,
    val prices: List<LabeledPrice>
)