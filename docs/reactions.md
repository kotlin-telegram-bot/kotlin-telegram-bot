# Reactions

Since Bot API 7.0 bots can attach reactions to messages with `setMessageReaction`, and — if the administrator grants the `message_reactions` allowed-update — receive `MessageReactionUpdated` events when users react. Bot API 7.9 added paid (star) reactions, and Bot API 10.0 added two new operations to remove reactions: `deleteMessageReaction` and `deleteAllMessageReactions`. This library exposes all of them directly on the `Bot` instance.

To set a reaction, pass a list of `ReactionType` values. For standard emoji reactions, use the actual Unicode emoji character (not a named constant):

```kotlin
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.reaction.ReactionType

val chatId = ChatId.fromId(CHAT_ID)

bot.setMessageReaction(
    chatId = chatId,
    messageId = messageId,
    reaction = listOf(ReactionType.Emoji("👍")), // thumbs up
    isBig = false,
)
```

Bot API 7.9 introduced star-paid reactions, which are sent as a singleton:

```kotlin
bot.setMessageReaction(
    chatId = chatId,
    messageId = messageId,
    reaction = listOf(ReactionType.Paid),
)
```

Bot API 10.0 lets you remove reactions. `deleteMessageReaction` removes the bot's own reaction by default, or another user's reaction if you pass a `userId`. `deleteAllMessageReactions` wipes every reaction on a message and requires the `can_manage_chat` administrator right.

```kotlin
bot.deleteMessageReaction(chatId, messageId)              // remove the bot's own reaction
bot.deleteMessageReaction(chatId, messageId, userId = 42) // remove a specific user's reaction
bot.deleteAllMessageReactions(chatId, messageId)          // requires can_manage_chat
```

On the receiving side, reaction updates arrive as `Update.messageReaction` (a `MessageReactionUpdated`) and aggregated counts as `Update.messageReactionCount` (a `MessageReactionCountUpdated`). The dispatcher DSL does not currently expose dedicated `messageReaction` / `messageReactionCount` handlers, so you can pick them off the raw `Update` inside any handler that has access to it — for example, by reading `update.messageReaction` from a generic `message` handler's environment, or by inspecting updates in a custom polling loop:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        message {
            update.messageReaction?.let { reaction ->
                // reaction: MessageReactionUpdated — react to it however you want
            }
            update.messageReactionCount?.let { counts ->
                // counts: MessageReactionCountUpdated — aggregated reaction totals
            }
        }
    }
}
```

Don't forget that reaction updates are not delivered by default: you need to list `message_reactions` (and/or `message_reaction_count`) in the bot's allowed updates when starting polling or registering a webhook.

For the full specification of every reaction type, see:

* https://core.telegram.org/bots/api#setmessagereaction
