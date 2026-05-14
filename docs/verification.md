# Verification

Bot API 8.2 lets bots that represent a verified organisation grant — and revoke — Telegram's "verified by organisation" badge on the users and chats they choose. The feature is gated server-side: only bots that have been explicitly registered as verified organisations may call these methods, and any other bot will receive an error back from the API. The library exposes the four operations directly on the `Bot` instance and they all return a plain `TelegramBotResult<Boolean>`.

To verify a user or a chat, call `verifyUser` or `verifyChat`. The optional `customDescription` is shown next to the badge instead of the organisation's default description, and the organisation itself must have been granted the right to override it on a per-target basis. Pass `null` (the default) to use the organisation's standard description:

```kotlin
import com.github.kotlintelegrambot.entities.ChatId

bot.verifyUser(
    userId = 12_345L,
    customDescription = "Official customer support agent",
)

bot.verifyChat(
    chatId = ChatId.fromId(CHANNEL_ID),
    customDescription = "Verified community channel",
)
```

To take the badge away again, call `removeUserVerification` or `removeChatVerification`. Both accept the same identifier you used to grant the badge, and both return `true` on success:

```kotlin
bot.removeUserVerification(userId = 12_345L)
bot.removeChatVerification(chatId = ChatId.fromId(CHANNEL_ID))
```

These operations are intended to be invoked from your back office — for example, after an internal review system green-lights a partner account — rather than from message handlers. They do not produce service messages in the affected chat: the badge simply appears (or disappears) on the user or chat profile.

A typical wrapper that handles both the happy path and a verification failure looks like this:

```kotlin
import com.github.kotlintelegrambot.entities.ChatId

fun verifyPartner(userId: Long, description: String?) {
    bot.verifyUser(userId, description).fold(
        ifSuccess = { /* badge granted */ },
        ifError = { error -> /* the bot is not a verified organisation, or the description is rejected */ },
    )
}

fun unverifyPartner(userId: Long) {
    bot.removeUserVerification(userId).fold(
        ifSuccess = { /* badge removed */ },
        ifError = { /* user was not verified by this organisation */ },
    )
}
```

For the full specification, including the exact behaviour around custom descriptions and the error codes returned to non-verified bots, see https://core.telegram.org/bots/api#verifyuser.
