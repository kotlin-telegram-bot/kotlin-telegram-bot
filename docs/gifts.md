# Gifts

Bots can send gifts to users and channels, and (since Bot API 9.0) manage the gifts owned by a connected business account. The catalogue of gifts the bot is allowed to send is retrieved with `bot.getAvailableGifts`, which returns a `Gifts` wrapper exposing the list as `Gifts.gifts: List<Gift>`.

```kotlin
val bot = bot {
    token = BOT_API_TOKEN
    // additional configuration
}

val catalogue = bot.getAvailableGifts()
// catalogue.fold({ it.gifts.forEach { gift -> println(gift.id) } }, { /* error */ })
```

Once you have picked a `Gift.id` from the catalogue, send it with `bot.sendGift`. The optional `payForUpgrade` flag lets the bot pre-pay the cost of turning a regular gift into a unique one, so the recipient gets the upgrade for free:

```kotlin
bot.sendGift(
    giftId = "gift-id",
    userId = 42L,
    text = "Happy birthday!",
    payForUpgrade = true,
)
```

When a user receives a gift inside a chat, the corresponding service message arrives on the regular update stream. Regular gifts populate `message.gift` (`GiftInfo`); unique gifts populate `message.uniqueGift` (`UniqueGiftInfo`):

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        message {
            message.gift?.let { info -> /* regular gift received */ }
            message.uniqueGift?.let { info -> /* unique gift received */ }
        }
    }
}
```

For business accounts the bot is connected to, you can browse and operate on the gifts the account owns: `bot.getBusinessAccountGifts` lists them, `bot.convertGiftToStars` exchanges a gift for its Star value, `bot.upgradeGift` turns a regular gift into a unique one, and `bot.transferGift` moves a unique gift to another chat.

For more information about gifts, see https://core.telegram.org/bots/api#sendgift.
