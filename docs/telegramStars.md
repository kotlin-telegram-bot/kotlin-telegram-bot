# Telegram Stars

Telegram Stars (currency code `XTR`) are the monetisation primitive for digital goods on Telegram. They power Mini Apps, paid media, subscriptions and gifts, and they let bots sell anything that lives entirely inside the Telegram ecosystem without a third-party payment provider. To charge in Stars you reuse the regular invoice flow with `bot.sendInvoice`, setting the currency to `XTR`.

```kotlin
val bot = bot {
    token = BOT_API_TOKEN
    // additional configuration
}

bot.sendInvoice(
    chatId = ChatId.fromId(USER_ID),
    paymentInvoiceInfo = PaymentInvoiceInfo(
        title = "Premium feature",
        description = "Unlock the premium feature for life.",
        payload = "sku-7",
        providerToken = "", // empty for Stars
        startParameter = "premium",
        currency = "XTR",
        prices = listOf(LabeledPrice(label = "Star", amount = 100)),
    ),
)
```

Once the user pays, the bot receives a `Message` carrying a `SuccessfulPayment`. You can listen to it from the dispatcher DSL and, if needed, refund the charge with `refundStarPayment`:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        message {
            val payment = message.successfulPayment ?: return@message
            // grant the purchased item to message.from?.id
            // and, if the user is not happy, refund it:
            bot.refundStarPayment(
                userId = message.from!!.id,
                telegramPaymentChargeId = payment.telegramPaymentChargeId,
            )
        }
    }
}
```

To inspect the bot's Star activity you have `getStarTransactions` for the transaction history and `getMyStarBalance` for the current balance:

```kotlin
val transactions = bot.getStarTransactions(offset = 0, limit = 100)
val balance = bot.getMyStarBalance()
```

For more information about Telegram Stars, see https://core.telegram.org/bots/payments-stars.
