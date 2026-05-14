# Link preview options

`LinkPreviewOptions` (Bot API 7.0) replaces the older `disableWebPagePreview` boolean on `sendMessage` and `editMessageText`. It lets you do more than just on/off — pick a different URL to preview, force a small or large preview, or put the preview above the message text.

The library exposes it as `com.github.kotlintelegrambot.entities.LinkPreviewOptions`. Both `sendMessage` and `editMessageText` accept `linkPreviewOptions: LinkPreviewOptions? = null` at the end of their signatures.

## Disable the preview entirely

```kotlin
bot.sendMessage(
    chatId = ChatId.fromId(chatId),
    text = "check https://example.com",
    linkPreviewOptions = LinkPreviewOptions(isDisabled = true),
)
```

## Override which URL is previewed

If the message text contains several links, Telegram picks the first by default. To force a different one:

```kotlin
bot.sendMessage(
    chatId = ChatId.fromId(chatId),
    text = "two links: https://first.example.com and https://second.example.com",
    linkPreviewOptions = LinkPreviewOptions(url = "https://second.example.com"),
)
```

## Force preview size

```kotlin
LinkPreviewOptions(preferSmallMedia = true)   // shrunk thumbnail
LinkPreviewOptions(preferLargeMedia = true)   // big banner
```

These are mutually exclusive. Telegram silently ignores them if the URL doesn't support resizing.

## Put the preview above the text

```kotlin
bot.sendMessage(
    chatId = ChatId.fromId(chatId),
    text = "After the preview",
    linkPreviewOptions = LinkPreviewOptions(
        url = "https://example.com",
        showAboveText = true,
        preferLargeMedia = true,
    ),
)
```

## Receiving — `Message.linkPreviewOptions`

When a user sends a message with custom preview options, those options arrive on `Message.linkPreviewOptions`:

```kotlin
dispatch {
    text {
        message.linkPreviewOptions?.let { opts ->
            println("link preview disabled=${opts.isDisabled} aboveText=${opts.showAboveText}")
        }
    }
}
```

## See also

- Telegram Bot API spec: [`LinkPreviewOptions`](https://core.telegram.org/bots/api#linkpreviewoptions).
- The legacy `disableWebPagePreview` parameter on `sendMessage` / `editMessageText` still works, but the new object is strictly richer.
