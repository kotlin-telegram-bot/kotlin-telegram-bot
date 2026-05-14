# Webhooks

Webhooks are the push alternative to long polling: instead of asking Telegram for updates, you give it a public HTTPS URL and Telegram delivers every `Update` as a POST request to that endpoint. The library configures the Telegram side of the integration through the `webhook { }` block of the bot DSL and lets you feed received updates back into the dispatcher.

```kotlin
val bot = bot {
    token = BOT_API_TOKEN

    webhook {
        url = "https://example.com/telegram/$BOT_API_TOKEN"
        certificate = TelegramFile.ByFile(File("public.pem"))
        maxConnections = 50
        allowedUpdates = listOf("message", "callback_query")
        dropPendingUpdates = true
        secretToken = "a-strong-shared-secret"
    }

    dispatch {
        // your handlers
    }
}

bot.startWebhook()
```

Calling `startWebhook()` performs a `setWebhook` API call with all the parameters above and then starts consuming the updates queue. If you already registered the webhook out of band (for example from a deploy script) and only want the library to process incoming updates, opt out of the automatic registration:

```kotlin
val bot = bot {
    token = BOT_API_TOKEN
    webhook {
        url = "https://example.com/telegram/$BOT_API_TOKEN"
        createOnStart = false
    }
}

bot.startWebhook()
```

The library does not ship an HTTP server: exposing the public endpoint that Telegram calls is your responsibility. The recommended pattern is to receive the request in whichever framework you use and forward the raw JSON body to the bot:

```kotlin
// inside your HTTP handler, after validating the X-Telegram-Bot-Api-Secret-Token header
suspend fun onTelegramRequest(rawJsonBody: String) {
    bot.processUpdate(rawJsonBody)
}
```

`processUpdate` accepts either a parsed `Update` or the raw JSON string and is `suspend`, which makes it equally suited for a Ktor route or a serverless function handler that wakes up only when Telegram posts an update. A complete Ktor-based reference is available in the [samples/webhook](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/tree/main/samples/webhook) directory.

The `secretToken` you set is sent back by Telegram in the `X-Telegram-Bot-Api-Secret-Token` header of every webhook request, so your endpoint can authenticate the caller and reject anything that does not come from Telegram. When you want to tear the webhook down, call `bot.stopWebhook()`.

For more information about webhook configuration and the underlying API call check https://core.telegram.org/bots/api#setwebhook.
