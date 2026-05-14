# Business Connection

Telegram Business lets users link a bot to their personal account so that the bot can act on their behalf in private chats. When the user authorises (or revokes) the connection, your bot receives a `business_connection` update carrying a `BusinessConnection` object. Its `id` is the handle every subsequent send or edit method accepts via the `businessConnectionId` parameter to operate as the business account.

The business updates are part of the regular `Update` payload, so you can read them directly from any handler that exposes the update:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        message {
            update.businessConnection?.let { /* connection created or updated */ }
            update.businessMessage?.let { /* new message in a connected chat */ }
            update.editedBusinessMessage?.let { /* message edited */ }
            update.deletedBusinessMessages?.let { /* one or more messages deleted */ }
        }
    }
}
```

Once you have a connection id you can send a message on behalf of the business account by forwarding it through the existing send methods. Every send/edit endpoint of the bot accepts the optional `businessConnectionId`:

```kotlin
bot.sendMessage(
    chatId = ChatId.fromId(bc.userChatId),
    text = "Hi from business!",
    businessConnectionId = bc.id,
)
```

`BusinessConnection.canReply` is preserved for backward compatibility but is deprecated since Bot API 9.0; check `BusinessConnection.rights` (a `BusinessBotRights` value) instead. Its boolean flags (`canReply`, `canReadMessages`, `canDeleteOutgoingMessages`, `canEditName`, `canManageStories`, ...) reflect exactly the permissions granted by the user at connect time.

Bot API 9.0 also introduced a long list of methods to manage the connected business account directly. They are all available on `Bot` and map 1:1 to the Telegram operations: `readBusinessMessage`, `deleteBusinessMessages`, `setBusinessAccountName`, `setBusinessAccountUsername`, `setBusinessAccountBio`, `setBusinessAccountProfilePhoto`, `setBusinessAccountGiftSettings`, `transferBusinessAccountStars`, `getBusinessAccountStarBalance`, `getBusinessAccountGifts`, `postStory`, `editStory` and `deleteStory`. Refer to the API spec for the full parameter list of each.

For more information about Telegram Business and the connection lifecycle check https://core.telegram.org/bots/api#businessconnection.
