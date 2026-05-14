# Suggested posts

Bot API 9.2 introduced suggested posts for direct messages topics in channels: users can propose a post, and the channel administrators (or a bot acting on their behalf) can approve or decline it. This library exposes both the administrative operations and the full set of service-message fields that describe a post's lifecycle.

On the receiving side, every stage of the lifecycle shows up as a field on the incoming `Message`. The post being suggested carries a `message.suggestedPostInfo` (`SuggestedPostInfo`); the subsequent decisions arrive as separate service messages with `message.suggestedPostApproved`, `message.suggestedPostApprovalFailed` or `message.suggestedPostDeclined`. Once an approved paid post goes through, you'll see `message.suggestedPostPaid` or, if the channel refunds the user, `message.suggestedPostRefunded`. The `message.isPaidPost` flag tells you whether the published post itself was a paid one:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        message {
            message.suggestedPostInfo?.let { info -> /* a post was suggested */ }
            message.suggestedPostApproved?.let { approved -> /* admin approved it */ }
            message.suggestedPostApprovalFailed?.let { failed -> /* approval could not be finalised */ }
            message.suggestedPostDeclined?.let { declined -> /* admin declined it */ }
            message.suggestedPostPaid?.let { paid -> /* a paid post was charged */ }
            message.suggestedPostRefunded?.let { refunded -> /* a paid post was refunded */ }

            if (message.isPaidPost == true) {
                // published post is a paid one
            }
        }
    }
}
```

To act on a suggested post, use `bot.approveSuggestedPost` or `bot.declineSuggestedPost`. The optional `sendDate` on approval schedules the post for a future moment (Unix timestamp, in seconds); the optional `comment` on decline gives the original author a reason:

```kotlin
import com.github.kotlintelegrambot.entities.ChatId

bot.approveSuggestedPost(
    chatId = ChatId.fromId(CHANNEL_ID),
    messageId = suggestedMessageId,
    sendDate = 1_768_000_000L, // optional: publish later
)

bot.declineSuggestedPost(
    chatId = ChatId.fromId(CHANNEL_ID),
    messageId = suggestedMessageId,
    comment = "Off-topic for this channel",
)
```

Suggested posts live inside direct-messages topics of a channel. The library models the topic as `DirectMessagesTopic` (exposing `topicId` and the optional `user` who owns the topic), and `Chat.isDirectMessages` lets you tell whether a given chat is a direct-messages chat in the first place — handy when routing updates by chat type:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        message {
            if (message.chat.isDirectMessages == true) {
                // this update came from a direct-messages topic in a channel
            }
        }
    }
}
```

For the full specification of approval, decline and paid-post payloads, see https://core.telegram.org/bots/api#approvesuggestedpost.
