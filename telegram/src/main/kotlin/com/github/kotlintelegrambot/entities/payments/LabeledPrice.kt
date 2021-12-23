package com.github.kotlintelegrambot.entities.payments

import java.math.BigInteger

public data class LabeledPrice(
    val label: String? = null,
    val amount: BigInteger
)
