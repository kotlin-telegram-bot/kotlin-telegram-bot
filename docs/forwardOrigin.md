# Forward origin

Bot API 7.0 reshaped how forwarded messages are represented. The legacy fields `forward_from`, `forward_from_chat`, `forward_from_message_id`, `forward_signature`, `forward_sender_name` and `forward_date` on `Message` are no longer populated by Telegram for new forwards. Use `message.forwardOrigin: MessageOrigin?` instead — `null` means the message wasn't forwarded.

`MessageOrigin` is a sealed class with four variants, all of them carrying the original send `date` (Unix time):

* `MessageOrigin.User` — forwarded from a regular user. Exposes `senderUser: User`.
* `MessageOrigin.HiddenUser` — forwarded from a user who has hidden their profile or has no username. Exposes only `senderUserName: String`.
* `MessageOrigin.Chat` — forwarded on behalf of a chat into a group. Exposes `senderChat: Chat` and an optional `authorSignature`.
* `MessageOrigin.Channel` — forwarded from a channel. Exposes `chat: Chat`, the original `messageId: Long`, and an optional `authorSignature`.

Because `MessageOrigin` is sealed, a `when` on it gives you exhaustive handling of the four cases. Don't forget the `null` branch for messages that aren't forwards at all:

```kotlin
import com.github.kotlintelegrambot.entities.MessageOrigin

dispatch {
    message {
        when (val origin = message.forwardOrigin) {
            is MessageOrigin.User -> {
                val name = origin.senderUser.firstName
                bot.sendMessage(ChatId.fromId(message.chat.id), text = "Forwarded from $name")
            }
            is MessageOrigin.HiddenUser -> {
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    text = "Forwarded from ${origin.senderUserName} (hidden profile)",
                )
            }
            is MessageOrigin.Chat -> {
                val signed = origin.authorSignature?.let { " signed $it" }.orEmpty()
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    text = "Forwarded from chat ${origin.senderChat.id}$signed",
                )
            }
            is MessageOrigin.Channel -> {
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    text = "Forwarded from channel ${origin.chat.title} (msg ${origin.messageId})",
                )
            }
            null -> {
                // not a forward; ignore
            }
        }
    }
}
```

If you previously relied on `Filter.Forward` to short-circuit non-forwarded messages, note that it still checks the legacy `forwardDate` field. For new code, branch directly on `message.forwardOrigin != null` to stay compatible with the 7.0+ representation.

Full specification: https://core.telegram.org/bots/api#messageorigin.
