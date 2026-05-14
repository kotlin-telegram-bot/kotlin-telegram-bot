# Error handling

Every API call in this library returns a `TelegramBotResult<T>` — a sealed type with a `Success` arm and an `Error` arm that further splits into `Error.TelegramApi` (Telegram returned an error payload), `Error.HttpError` (non-2xx HTTP response), `Error.InvalidResponse` (the body couldn't be mapped to a Bot API response), and `Error.Unknown` (any exception thrown while executing or processing the call). This means a single failed `sendMessage` never throws — the exception is caught and translated into the matching `Error` value. For errors that happen during update polling (i.e. before any single API call exists), the dispatcher exposes a separate `telegramError { }` handler.

Use `fold` to handle a result inline. The two lambdas are `ifSuccess` and `ifError`, in that order:

```kotlin
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.types.TelegramBotResult

bot.sendMessage(ChatId.fromId(CHAT_ID), "hi").fold(
    ifSuccess = { sent -> println("ok ${sent.messageId}") },
    ifError = { err ->
        when (err) {
            is TelegramBotResult.Error.TelegramApi ->
                println("API error ${err.errorCode}: ${err.description}")
            is TelegramBotResult.Error.HttpError ->
                println("HTTP ${err.httpCode}: ${err.description}")
            is TelegramBotResult.Error.InvalidResponse ->
                println("Invalid response (HTTP ${err.httpCode})")
            is TelegramBotResult.Error.Unknown ->
                println("Unknown: ${err.exception}")
        }
    },
)
```

If you only care about one side, `onSuccess { }` and `onError { }` are non-terminal helpers that return the original result so you can chain them. There is also `getOrNull()`, `get()` (throws if it isn't a `Success`) and a `getOrDefault` infix extension when you want to fall back to a default value.

For polling-time errors — network blips, malformed `getUpdates` responses, or any other failure that doesn't belong to a specific API call — register a `telegramError` handler on the dispatcher. The handler's `error` value is a `TelegramError` with a `getErrorMessage()` method:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        telegramError {
            println("polling error: ${error.getErrorMessage()}")
        }
    }
}
```

For the full list of possible Telegram error codes and a description of how the Bot API reports failures, see:

* https://core.telegram.org/bots/api#making-requests
