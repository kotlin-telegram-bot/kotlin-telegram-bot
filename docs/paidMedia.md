# Paid Media

Paid media lets you publish a teaser to a channel where viewers must pay a configurable amount of Telegram Stars to unlock the original photos or videos. The library exposes it through `bot.sendPaidMedia`, which accepts a list of `InputPaidMedia` (`Photo` or `Video`) together with the Star price and an optional caption. The target chat must be one where the bot is an administrator.

```kotlin
val bot = bot {
    token = BOT_API_TOKEN
    // additional configuration
}

bot.sendPaidMedia(
    chatId = ChatId.fromChannelUsername("@my_channel"),
    starCount = 50,
    media = listOf(
        InputPaidMedia.Photo(media = "AgACAgIAAxk..."), // file_id
        InputPaidMedia.Video(
            media = "BAACAgIAAxk...",                   // file_id
            cover = "AgACAgIAAxk...",                   // optional cover photo file_id
            startTimestamp = 5,                          // optional preview offset (s)
        ),
    ),
    caption = "Behind the scenes of episode 7",
    showCaptionAboveMedia = true,
)
```

The caption can include `parseMode` or pre-built `captionEntities` the same way as in `sendPhoto` or `sendVideo`. When a user pays for the album, the bot receives an update whose `purchasedPaidMedia` field is set to a `PaidMediaPurchased(from, paidMediaPayload)`. You can read the payload you originally sent and grant access accordingly:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        update {
            val purchase = update.purchasedPaidMedia ?: return@update
            // purchase.from is the buyer, purchase.paidMediaPayload echoes the
            // string you passed as `payload` when calling sendPaidMedia.
        }
    }
}
```

For more information about paid media, see https://core.telegram.org/bots/api#sendpaidmedia.
