# Reply parameters

Since Bot API 7.0 the way to reply to a previous message is the `ReplyParameters` object. It replaces the legacy `replyToMessageId` + `allowSendingWithoutReply` send-method parameters and additionally supports cross-chat replies, quoting an exact substring of the original message, and (since 9.x) checklist-task and poll-option replies.

The library exposes it as `com.github.kotlintelegrambot.entities.ReplyParameters` and every canonical `send*` and `copyMessage` method accepts an optional `replyParameters: ReplyParameters? = null` argument at the end of its signature.

## Reply to a message in the same chat

```kotlin
bot.sendMessage(
    chatId = ChatId.fromId(chatId),
    text = "yes, exactly",
    replyParameters = ReplyParameters(messageId = otherMessage.messageId),
)
```

## Reply to a message in a different chat

```kotlin
bot.sendMessage(
    chatId = ChatId.fromId(receiverChatId),
    text = "see this:",
    replyParameters = ReplyParameters(
        messageId = sourceMessage.messageId,
        chatId = ChatId.fromId(sourceChatId),
    ),
)
```

## Quote a substring of the original message

The `quote` field must be an **exact substring** of the original message text after entity parsing.

```kotlin
bot.sendMessage(
    chatId = ChatId.fromId(chatId),
    text = "this part in particular",
    replyParameters = ReplyParameters(
        messageId = otherMessage.messageId,
        quote = "the substring being quoted",
        quotePosition = 12, // UTF-16 offset into the original text, optional
    ),
)
```

You can format the quote with `quoteParseMode = ParseMode.MARKDOWN_V2` or pass a `quoteEntities: List<MessageEntity>?` for explicit entity control.

## Send even if the original is gone

```kotlin
ReplyParameters(
    messageId = otherMessage.messageId,
    allowSendingWithoutReply = true,
)
```

## Receiving — `Message.externalReply` and `Message.quote`

When a user quotes a message from another chat, your incoming `Message` carries:
- `Message.externalReply: ExternalReplyInfo?` — the source message metadata (origin, chat, message_id, attachments).
- `Message.quote: TextQuote?` — the literal text the user highlighted, with entities and position.

```kotlin
dispatch {
    text {
        message.quote?.let { quote ->
            println("user quoted: ${quote.text}")
        }
        message.externalReply?.let { reply ->
            println("from origin: ${reply.origin}")
        }
    }
}
```

## See also

- Telegram Bot API spec: [`ReplyParameters`](https://core.telegram.org/bots/api#replyparameters), [`ExternalReplyInfo`](https://core.telegram.org/bots/api#externalreplyinfo), [`TextQuote`](https://core.telegram.org/bots/api#textquote).
